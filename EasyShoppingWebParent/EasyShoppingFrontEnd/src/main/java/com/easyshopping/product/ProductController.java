package com.easyshopping.product;

import com.easyshopping.category.CategoryService;
import com.easyshopping.common.entity.Category;
import com.easyshopping.common.entity.product.Product;
import com.easyshopping.common.exception.CategoryNotFoundException;
import com.easyshopping.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private ProductService productService;
    private CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/category/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String category_alias, Model model) throws CategoryNotFoundException {
        return viewCategoryByPage(category_alias, 1, model);
    }

    @GetMapping("/category/{category_alias}/page/{pageNumber}")
    public String viewCategoryByPage(@PathVariable("category_alias") String category_alias, @PathVariable("pageNumber") int pageNumber, Model model) throws CategoryNotFoundException {
        try {
            Category category = categoryService.getCategoryByAlias(category_alias);
            /*for breadcrumbs nav menu*/
            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            Page<Product> pageProducts = productService.listProductByCategory(pageNumber, category.getId());
            List<Product> productList = pageProducts.getContent();

            long startCount = (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("productList", productList);

            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);

            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("category", category);
            return "product/products_by_category";
        } catch (CategoryNotFoundException ex) {
            return "error/404";
        }
    }

    @GetMapping("/product/{product_alias}")
    public String viewProductDetails(@PathVariable("product_alias") String alias, Model model){
        try {
            Product product = productService.getProduct(alias);
            /*for breadcrumbs nav menu*/
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("pageTitle", product.getShortName());
            return "product/product_detail";
        }catch (ProductNotFoundException ex){
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@RequestParam("keyword") String keyword, Model model){
        return searchByPage(keyword,1,model);
    }

    @GetMapping("/search/page/{pageNumber}")
    public String searchByPage(@RequestParam("keyword") String keyword,@PathVariable("pageNumber") int pageNumber, Model model){
        Page<Product> pageProducts = productService.search(keyword, pageNumber);
        List<Product> listResult = pageProducts.getContent();

        long startCount = (pageNumber - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("pageTitle", keyword + " - Search Result");

        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);
        return "product/search_result";
    }
}
