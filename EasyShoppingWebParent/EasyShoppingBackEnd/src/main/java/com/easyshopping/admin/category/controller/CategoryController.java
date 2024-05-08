package com.easyshopping.admin.category.controller;

import com.easyshopping.admin.category.domain.CategoryPageInfo;
import com.easyshopping.common.exception.CategoryNotFoundException;
import com.easyshopping.admin.category.exporter.CategoryCsvExporter;
import com.easyshopping.admin.category.exporter.CategoryExcelExporter;
import com.easyshopping.admin.category.exporter.CategoryPdfExporter;
import com.easyshopping.admin.category.service.CategoryService;
import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.admin.serviceCommon.ServiceCommon;
import com.easyshopping.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Set;

@Controller
public class CategoryController {

   private CategoryService categoryService;

   private ServiceCommon serviceCommon;

    @Autowired
    public CategoryController(CategoryService categoryService, ServiceCommon serviceCommon) {
        this.categoryService = categoryService;
        this.serviceCommon = serviceCommon;
    }

    @GetMapping("/categories")
    public String listFirstPage(@RequestParam(value = "sortDirection", required = false) String sortDirection, Model model) {
        return listByPage(1, "name", sortDirection,null, model);
    }

    @GetMapping("/categories/page/{pageNumber}")
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, @RequestParam(value = "sortField", required = false) String sortField, @RequestParam(value = "sortDirection" , required = false) String sortDirection, @RequestParam(value = "keyword", required = false) String keyword,Model model) {
        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";
        }
        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> listCategories = categoryService.listByPage(categoryPageInfo, pageNumber, sortField,sortDirection, keyword);

        String reverseDirection = sortDirection.equals("asc") ? "desc" : "asc";

        long startCount = (pageNumber - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > categoryPageInfo.getTotalElements()) {
            endCount = categoryPageInfo.getTotalElements();
        }

        model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("listCategories", listCategories);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseDirection", reverseDirection);

        model.addAttribute("keyword", keyword);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")/*@RequestParam("fileImage") - прописується в HTML в атрибуті name*/
    public RedirectView saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws IOException, URISyntaxException {
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        boolean checkID = false;
        if (category.getId() !=  null) {
            checkID = true;
        }
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            if (fileName.contains(" ")) {
                fileName = fileName.replaceAll(" ", "_");
            }
            category.setImage(fileName);
            Category categorySaved = categoryService.save(category);

            String uploadDir = "category-images/" + categorySaved.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (category.getImage().isEmpty()) {
                category.setImage(null);
            }
            categoryService.save(category);
        }

        if(checkID) {
            redirectAttributes.addFlashAttribute("message", "The category with ID: "+category.getId()+" has been updated successfully");
        }

        if(checkID == false){
            redirectAttributes.addFlashAttribute("message", "The new category has been saved successfully");
        }
        return serviceCommon.getRedirectViewPostRequest(httpServletRequest, redirect, "categories", checkID, category);
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/delete/{id}")
    public RedirectView deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws URISyntaxException {
        try {
            categoryService.delete(id);
            String categoryDirectory = "category-images/" + id;
            FileUploadUtil.removeDir(categoryDirectory);
            redirectAttributes.addFlashAttribute("message",
                    "The category ID " + id + " has been deleted successfully");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        return serviceCommon.getRedirectViewGetRequest(httpServletRequest, redirect, "categories");
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public RedirectView updateCategoryEnabled(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) throws CategoryNotFoundException, URISyntaxException {
        categoryService.updateCategoryEnabled(id, enabled);
        String status = enabled ? "enabled" : "disabled";

        Category category = categoryService.get(id);
        Set<Category> categoryChildren = category.getChildren();
        String message = null;

        if (categoryChildren.isEmpty()){
            message = "The category ID " + id + " has been " + status;
            redirectAttributes.addFlashAttribute("message", message);
        } else {
            message = "The category ID " + id + " and all its children have been " + status;
            redirectAttributes.addFlashAttribute("message", message);
        }
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        return serviceCommon.getRedirectViewGetRequest(httpServletRequest, redirect, "categories");
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse httpServletResponse) throws IOException {
        List<Category> categories = categoryService.listCategoriesForExport();
        CategoryCsvExporter csvExporter = new CategoryCsvExporter();
        csvExporter.export(categories, httpServletResponse);
    }

    @GetMapping("/categories/export/excel")
    public void exportToExcel(HttpServletResponse httpServletResponse) throws IOException {
        List<Category> categories = categoryService.listCategoriesForExport();;
        CategoryExcelExporter excelExporter = new CategoryExcelExporter();
        excelExporter.export(categories, httpServletResponse);
    }

    @GetMapping("/categories/export/pdf")
    public void exportToPDF(HttpServletResponse httpServletResponse) throws IOException {
        List<Category> categories = categoryService.listCategoriesForExport();
        CategoryPdfExporter pdfExporter = new CategoryPdfExporter();
        pdfExporter.export(categories, httpServletResponse);
    }
}
