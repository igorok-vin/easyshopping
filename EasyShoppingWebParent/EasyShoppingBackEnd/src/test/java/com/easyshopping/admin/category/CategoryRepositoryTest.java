package com.easyshopping.admin.category;

import com.easyshopping.admin.category.repository.CategoryRepository;
import com.easyshopping.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    public void testCreateRootCategory1() {
        Category category = new Category("Computers");
        Category result = repository.save(category);

        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("Computers");
    }

    @Test
    public void testCreateRootCategory2() {
        Category category = new Category("Electronics");
        Category result = repository.save(category);

        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    public void testCreateSubCategory1() {
        Category parentCategory = new Category(1);
        Category subCategory = new Category("Desktops", parentCategory);
        Category result = repository.save(subCategory);

        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("Desktops");
    }

    @Test
    public void testCreateSubCategory1_1() {
        Category parentCategory = new Category(1);
        Category subCategory1 = new Category("Laptops", parentCategory);
        Category subCategory2 = new Category("Components", parentCategory);
        repository.saveAll(List.of(subCategory1, subCategory2));
    }

    @Test
    public void testCreateSubCategory2() {
        Category parentCategory = new Category(2);
        Category subCategory1 = new Category("Cameras", parentCategory);
        Category subCategory2 = new Category("Smartphones", parentCategory);
        repository.saveAll(List.of(subCategory1, subCategory2));
    }

    @Test
    public void testCreateSubCategoryForSubCategory() {
        Category parentSubCategory = new Category(5);
        Category childSubCategory = new Category("Memory", parentSubCategory);
        Category result = repository.save(childSubCategory);

        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("Memory");
    }

    @Test
    public void testGetCategory() {
        Category category = repository.findById(1).get();
        System.out.println(category.getName());
        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }
        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = repository.findAll();
        for (Category category : categories) {
            /*And we check a root category by examining the parent object of this category.*/
            if(category.getParent()==null){
                System.out.println(category.getName());
                /*And then we iterate through each child.*/
                Set<Category> children = category.getChildren();
                for (Category subCategory:children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel){
        int newSubLevel = subLevel +1;
        Set<Category> categorySet = parent.getChildren();
        for (Category subCategory: categorySet) {
            for (int i=0; i<newSubLevel; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());
            printChildren(subCategory,subLevel);
        }
    }

    @Test
    public void testRootCategories() {
        List<Category> rootCategories = repository.findRootCategories(Sort.by("name").ascending());
        rootCategories.forEach(cat-> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName() {
        String name = "Computers";
        Category category = repository.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    public void testFindByAlias() {
        String alias = "computers";
        Category category = repository.findByAlias(alias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }

}
