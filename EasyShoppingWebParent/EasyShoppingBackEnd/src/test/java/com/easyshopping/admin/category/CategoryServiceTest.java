package com.easyshopping.admin.category;

import com.easyshopping.admin.category.repository.CategoryRepository;
import com.easyshopping.admin.category.service.CategoryService;
import com.easyshopping.common.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckUniqueInNewModeReturnDuplicatedName() {
        Integer id = null;
        String name =  "computers";
        String alias = "abc";
        Category category = new Category(id,name,alias);

        when(categoryRepository.findByName(name)).thenReturn(category);
        String result = categoryService.checkUniqueCategoryNameAndAlias(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }
    @Test
    public void testCheckUniqueInNewModeReturnDuplicatedAlias() {
        Integer id = null;
        String name =  "computers";
        String alias = "abc";
        Category category = new Category(id,name,alias);

        when(categoryRepository.findByAlias(alias)).thenReturn(category);
        String result = categoryService.checkUniqueCategoryNameAndAlias(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name =  "computers";
        String alias = "abc";

        when(categoryRepository.findByAlias(alias)).thenReturn(null);
        String result = categoryService.checkUniqueCategoryNameAndAlias(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicatedAlias() {
        Integer id = 1;
        String name =  "computers";
        String alias = "abc";
        Category category = new Category(2,name,alias);

        when(categoryRepository.findByAlias(alias)).thenReturn(category);
        String result = categoryService.checkUniqueCategoryNameAndAlias(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicatedName() {
        Integer id = 1;
        String name =  "computers";
        String alias = "abc";
        Category category = new Category(id,name,alias);

        when(categoryRepository.findByName(name)).thenReturn(category);
        String result = categoryService.checkUniqueCategoryNameAndAlias(2, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInEditReturnOK() {
        Integer id = 1;
        String name =  "computers";
        String alias = "abc";
        Category category = new Category(id,name,alias);
        when(categoryRepository.findByAlias(alias)).thenReturn(category);
        String result = categoryService.checkUniqueCategoryNameAndAlias(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }

}
