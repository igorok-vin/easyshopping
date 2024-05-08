package com.easyshopping.admin.product.controller;

import com.easyshopping.admin.brand.service.BrandService;
import com.easyshopping.admin.category.service.CategoryService;
import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.common.exception.ProductNotFoundException;
import com.easyshopping.admin.product.service.ProductService;
import com.easyshopping.admin.security.EasyShoppingUserDetails;
import com.easyshopping.admin.serviceCommon.ServiceCommon;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import com.easyshopping.common.entity.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Controller
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private ProductService productService;
    private BrandService brandService;
    private ServiceCommon serviceCommon;
    private CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, BrandService brandService, ServiceCommon serviceCommon,CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.serviceCommon = serviceCommon;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listFirstPage(@RequestParam(value = "sortDirection", required = false) String sortDirection, Model model) {
        return listByPage(1, "name","asc", null,null, model);
    }

    @GetMapping("/products/page/{pageNumber}")
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, @RequestParam(value = "sortField", required = false) String sortField, @RequestParam(value = "sortDirection", required = false) String sortDirection, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {

        Page<Product> page = productService.listByPage(pageNumber,sortField, sortDirection, keyword,categoryId);
        List<Product> productList = page.getContent();
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        String reverseDirection = sortDirection.equals("asc") ? "desc" : "asc";

        long startCount = (pageNumber - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        if(categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("productList", productList);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseDirection", reverseDirection);
        model.addAttribute("keyword", keyword);

        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }


    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAllByName();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);
        return "products/product_form";
    }

    @PostMapping(value = "/products/save")
    public RedirectView saveProduct(Product product,
                                    @RequestParam(value = "fileImage",required = false) MultipartFile mainImageMultipartFile,
                                    @RequestParam(value = "nameExtraImage",required = false) MultipartFile[] extraImagesMultipartFile,
                                    @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                                    @RequestParam(name = "detailNames", required = false) String[] detailNames,
                                    @RequestParam(name = "detailValues", required = false) String[] detailValues,
                                    @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                                    @RequestParam(name = "imageNames", required = false) String[] imageNames,
                                    @AuthenticationPrincipal EasyShoppingUserDetails loggedUserRole,
                                    RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws IOException, URISyntaxException, ServletException {

        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);
        boolean checkID = false;

       if (product.getId() != null) {
            checkID = true;
        }

        if(loggedUserRole.hasRole("Salesperson")){
            productService.saveProductPrice(product);

            if(checkID) {
                redirectAttributes.addFlashAttribute("message", "The product with ID: "+product.getId()+" has been updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("message", "The new product has been saved successfully");
            }

            return serviceCommon.getRedirectViewPostRequest(httpServletRequest, redirect, "products", checkID, product);
        }

        ProductService.setMainImageName(mainImageMultipartFile, product);

        /*Тут працюєм з Extra Image секція для режиму редагування.В режимі редагування робимо set поточних картинок через imageIDs,imageNames після всіх маніпуляцій з ними щоб подальшому зберегти їх в базу*/
        ProductService.setExistingExtraImageNames(imageIDs, imageNames, product);

        /*Тут працюєм з Extra Image секція для створення  нового продукта не режим редагування а вежимі редагування буде остання секція, можна додатково додавати картинки. Тут динамічний ID на сторінці на основі поточного прочитаного списку фото і його розмір. */
        ProductService.setNewExtaImages(extraImagesMultipartFile, product);

        ProductService.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product productSaved = productService.saveProduct(product);

        ProductService.saveUploadedImages(mainImageMultipartFile, extraImagesMultipartFile, productSaved);

        ProductService.deleteExtraImagesWereRemovedOnForm(product);

        if(checkID) {
            redirectAttributes.addFlashAttribute("message", "The product with ID: "+product.getId()+" has been updated successfully");
        }

        if(checkID== false){
            redirectAttributes.addFlashAttribute("message", "The new product has been saved successfully");
        }

        return serviceCommon.getRedirectViewPostRequest(httpServletRequest, redirect, "products", checkID, product);
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabled(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, Model model, HttpServletRequest httpServletRequest) {
        productService.updateProductEnabled(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + id + " has been " + status;

        int pageNumber = Integer.parseInt(httpServletRequest.getParameter("page"));
        String sortField = httpServletRequest.getParameter("sortField");
        String sortDirection = httpServletRequest.getParameter("sortDirection");
        String keyword = httpServletRequest.getParameter("keyword");
        String categoryIdString = httpServletRequest.getParameter("categoryId");
        Integer categoryId = null;
        if(categoryIdString != null){
            categoryId=Integer.parseInt(categoryIdString);
        }
        model.addAttribute("message", message);
        if (keyword == null & categoryId == null) {
            return listByPage(pageNumber, sortField, sortDirection, null,null, model);
        } else if(keyword == null) {
            return listByPage(pageNumber, sortField, sortDirection, null,categoryId, model);
        } else if(categoryId == null) {
            return listByPage(pageNumber, sortField, sortDirection, keyword,null, model);
        } else {
            return listByPage(pageNumber, sortField, sortDirection, keyword,categoryId, model);
        }
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAllByName();
            Integer numberOfExistingExtraImages = product.getImagesExtra().size();

            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
            return "products/product_form";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/delete/{id}")
    public RedirectView deleteProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws URISyntaxException {
        try {
            productService.delete(id);
            String productImagesExtra = "product-images/" + id + "/extras";
            FileUploadUtil.removeDir(productImagesExtra);
            String productImagesMainDirectory = "product-images/" + id;
            FileUploadUtil.removeDir(productImagesMainDirectory);
            redirectAttributes.addFlashAttribute("message",
                    "The product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(true);

        int pageNumber = Integer.parseInt(httpServletRequest.getParameter("page"));
        String sortField = httpServletRequest.getParameter("sortField");
        String sortDirection = httpServletRequest.getParameter("sortDirection");
        String keyword = httpServletRequest.getParameter("keyword");
        String categoryId = httpServletRequest.getParameter("categoryId");

        if (keyword == null & categoryId == null) {
            redirect.setUrl("/products/page/" + pageNumber + "?sortField=" + sortField + "&sortDirection=" + sortDirection);
            return redirect;
        } else if (keyword == null) {
            redirect.setUrl("/products/page/" + pageNumber + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&categoryId=" + categoryId);
            return redirect;
        } else if (categoryId == null) {
            redirect.setUrl("/products/page/" + pageNumber + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&keyword=" + keyword);
            return redirect;
        } else {
            redirect.setUrl("/products/page/" + pageNumber + "?sortField=" + sortField + "&sortDirection=" + sortDirection + "&categoryId=" + categoryId + "&keyword=" + keyword);
            return redirect;
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }
}
