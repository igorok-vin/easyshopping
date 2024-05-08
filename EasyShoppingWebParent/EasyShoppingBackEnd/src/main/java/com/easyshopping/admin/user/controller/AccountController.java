package com.easyshopping.admin.user.controller;

import com.easyshopping.admin.security.EasyShoppingUserDetails;
import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.admin.user.service.UserService;
import com.easyshopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {
    private UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal EasyShoppingUserDetails loggedUser, Model model) {
        String email = loggedUser.getUsername();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
    return "users/account_form";
    }

    @PostMapping("/account/update")
    public String updateUserAccount(User user, RedirectAttributes redirectAttributes,@AuthenticationPrincipal EasyShoppingUserDetails loggedUser, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.updateUserAccount(user);

            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.updateUserAccount(user);
        }
        loggedUser.setLastName(user.getLastName());
        loggedUser.setFirstName(user.getFirstName());
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");
        return "redirect:/account";
    }
}
