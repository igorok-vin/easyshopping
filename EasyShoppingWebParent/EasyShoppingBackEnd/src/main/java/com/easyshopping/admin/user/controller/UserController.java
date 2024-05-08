package com.easyshopping.admin.user.controller;

import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.admin.serviceCommon.ServiceCommon;
import com.easyshopping.admin.user.exception.UserNotFoundException;
import com.easyshopping.admin.user.service.UserService;
import com.easyshopping.admin.user.exporter.UserCsvExporter;
import com.easyshopping.admin.user.exporter.UserExcelExporter;
import com.easyshopping.admin.user.exporter.UserPdfExporter;
import com.easyshopping.common.entity.Role;
import com.easyshopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Controller
public class UserController {

    private UserService userService;
    private ServiceCommon serviceCommon;

    @Autowired
    public UserController(UserService userService, ServiceCommon serviceCommon) {
        this.userService = userService;
        this.serviceCommon = serviceCommon;
    }

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "id", "asc", null);
    }

    @GetMapping("/users/page/{pageNumber}")
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model, @RequestParam(value = "sortField", required = false) String sortField, @RequestParam(value = "sortDirection", required = false) String sortDirection, @RequestParam(value = "keyword", required = false) String keyword) {

        Page<User> page = userService.listByPage(pageNumber, sortField, sortDirection, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNumber - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        String reverseDirection = sortDirection.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseDirection", reverseDirection);

        model.addAttribute("keyword", keyword);

        return "users/users";
    }


    @GetMapping("/users/new")
    public String createNewUser(Model model) {
        List<Role> listRoles = userService.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public RedirectView /*String*/ saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile, HttpServletRequest httpServletRequest) throws IOException, URISyntaxException {

        boolean checkID = false;
        if (user.getId() != null) {
            checkID = true;
        }

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.save(user);
        }

        if(checkID) {
            redirectAttributes.addFlashAttribute("message", "The user with ID: "+user.getId()+" has been updated successfully");
        }

        if(checkID == false){
            redirectAttributes.addFlashAttribute("message", "The new user has been saved successfully");
        }
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);

        return serviceCommon.getRedirectViewPostRequest(httpServletRequest, redirect,"users",checkID,user);
    }

    private String getSavingResult(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDirection=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUserProfile(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.get(id);
            List<Role> listRoles = userService.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public RedirectView deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws URISyntaxException {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        return serviceCommon.getRedirectViewGetRequest(httpServletRequest, redirect, "users");
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabled(@PathVariable("id") Integer id,
                                    @PathVariable("status") boolean enabled, Model model, HttpServletRequest httpServletRequest) {
        userService.updateUserEnabled(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;

        int pageNumber = Integer.parseInt(httpServletRequest.getParameter("page"));
        String sortField = httpServletRequest.getParameter("sortField");
        String sortDirection = httpServletRequest.getParameter("sortDirection");
        String keyword = httpServletRequest.getParameter("keyword");

        model.addAttribute("message", message);
        if (keyword == null) {
            return listByPage(pageNumber, model, sortField, sortDirection, null);
        } else {
            return listByPage(pageNumber, model, sortField, sortDirection, keyword);
        }
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse httpServletResponse) throws IOException {
        List<User> users = userService.listAll();
        UserCsvExporter userCsvExporter = new UserCsvExporter();
        userCsvExporter.export(users, httpServletResponse);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse httpServletResponse) throws IOException {
        List<User> users = userService.listAll();
        UserExcelExporter excelExporter = new UserExcelExporter();
        excelExporter.export(users, httpServletResponse);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse httpServletResponse) throws IOException {
        List<User> users = userService.listAll();
        UserPdfExporter userPdfExporter = new UserPdfExporter();
        userPdfExporter.export(users, httpServletResponse);
    }
}
