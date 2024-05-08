package com.easyshopping.admin.brand.controller;

import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.admin.brand.exception.BrandNotFoundException;
import com.easyshopping.admin.brand.exporter.BrandCsvExporter;
import com.easyshopping.admin.brand.exporter.BrandExcelExporter;
import com.easyshopping.admin.brand.exporter.BrandPdfExporter;
import com.easyshopping.admin.brand.service.BrandService;
import com.easyshopping.admin.category.service.CategoryService;
import com.easyshopping.admin.serviceCommon.ServiceCommon;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
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
public class BrandController {

    private BrandService brandService;
    private CategoryService categoryService;
    private ServiceCommon serviceCommon;

    @Autowired
    public BrandController(BrandService brandService, CategoryService categoryService, ServiceCommon serviceCommon) {
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.serviceCommon = serviceCommon;
    }

    @GetMapping("/brands")
    public String listFirstPage(@RequestParam(value = "sortDirection", required = false) String sortDirection, Model model) {
        return listByPage(1, "name", "asc",null, model);
    }

    @GetMapping("/brands/page/{pageNumber}")
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, @RequestParam(value = "sortField", required = false) String sortField, @RequestParam(value = "sortDirection") String sortDirection, @RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";
        }
        Page<Brand> page = brandService.listByPage(pageNumber, sortDirection, keyword);

        List<Brand> listBrands = page.getContent();

        String reverseDirection = sortDirection.equals("asc") ? "desc" : "asc";

        long startCount = (pageNumber - 1) * BrandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("listBrands", listBrands);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", "name");
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseDirection", reverseDirection);

        model.addAttribute("keyword", keyword);

        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String addNewBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Add New Brand");
        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public RedirectView saveNewBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws IOException, URISyntaxException {
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        boolean checkID = false;
        if (brand.getId() !=  null) {
            checkID = true;
        }
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);
            Brand brandSaved = brandService.save(brand);

            String uploadDir = "brand-logo/" + brandSaved.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (brand.getLogo().isEmpty()) {
                brand.setLogo(null);
            }
            brandService.save(brand);
        }

        if(checkID) {
            redirectAttributes.addFlashAttribute("message", "The brand with ID: "+brand.getId()+" has been updated successfully");
        }

        if(checkID == false){
            redirectAttributes.addFlashAttribute("message", "The new brand has been saved successfully");
        }

        return serviceCommon.getRedirectViewPostRequest(httpServletRequest, redirect, "brands", checkID, brand);
    }

    @GetMapping("/brand/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
            return "brands/brand_form";
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brand/delete/{id}")
    public RedirectView deleteBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws URISyntaxException {
        try {
            brandService.delete(id);
            String categoryDirectory = "brand-logo/" + id;
            FileUploadUtil.removeDir(categoryDirectory);
            redirectAttributes.addFlashAttribute("message",
                    "The brand ID " + id + " has been deleted successfully");
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        return serviceCommon.getRedirectViewGetRequest(httpServletRequest, redirect, "brands");
    }

    @GetMapping("/brands/export/csv")
    public void exportToCSV(HttpServletResponse httpServletResponse) throws IOException {
        List<Brand> brands = brandService.listAll();
        BrandCsvExporter csvExporter = new BrandCsvExporter();
        csvExporter.export(brands, httpServletResponse);
    }

    @GetMapping("/brands/export/excel")
    public void exportToExel(HttpServletResponse httpServletResponse) throws IOException {
        List<Brand> brands = brandService.listAll();
        BrandExcelExporter csvExporter = new BrandExcelExporter();
        csvExporter.export(brands, httpServletResponse);
    }

    @GetMapping("/brands/export/pdf")
    public void exportToPdf(HttpServletResponse httpServletResponse) throws IOException {
        List<Brand> brands = brandService.listAll();
        BrandPdfExporter csvExporter = new BrandPdfExporter();
        csvExporter.export(brands, httpServletResponse);
    }
}
