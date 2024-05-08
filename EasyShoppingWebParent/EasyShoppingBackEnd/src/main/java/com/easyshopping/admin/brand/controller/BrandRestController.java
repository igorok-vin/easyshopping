package com.easyshopping.admin.brand.controller;

import com.easyshopping.admin.brand.exception.BrandNotFoundException;
import com.easyshopping.admin.brand.exception.BrandNotFoundRestException;
import com.easyshopping.admin.brand.service.BrandService;
import com.easyshopping.admin.category.domain.CategoryDTO;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    private BrandService brandService;

    @Autowired
    public BrandRestController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/brands/check_unique")
    public String checkUnique(@RequestParam(value = "id", required = false) Integer id, @RequestParam("name") String name) {
        return brandService.checkUniqueBrandName(id,name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name="id") Integer brandId) throws BrandNotFoundRestException {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        try {
            Brand brand = brandService.get(brandId);
            Set<Category> categories = brand.getCategories();
            for (Category category: categories) {
                CategoryDTO dto = new CategoryDTO(category.getId(),category.getName());
                categoryDTOList.add(dto);
            }
            return categoryDTOList;
        } catch (BrandNotFoundException e) {
           throw new BrandNotFoundRestException();
        }
    }
}
