package com.easyshopping.admin.brand;

import com.easyshopping.admin.brand.repository.BrandRepository;
import com.easyshopping.admin.category.repository.CategoryRepository;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreatBrand1() {
        Category laptops = categoryRepository.findByName("laptops");
        Brand acer = new Brand("Acer");
        acer.getCategories().add(laptops);
        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreatBrand2() {
        Category smartphones = categoryRepository.findByName("smartphones");
        Category laptops = categoryRepository.findByName("laptops");
        Category desktops = categoryRepository.findByName("desktops");
        Brand apple = new Brand("Apple");
        apple.getCategories().add(laptops);
        apple.getCategories().add(smartphones);
        apple.getCategories().add(desktops);
        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreatBrand3() {
        Category memory = categoryRepository.findByName("memory");
        Category smartphones = categoryRepository.findByName("smartphones");
        Category hard_drive = categoryRepository.findByName("hard drive");
        Brand samsung = new Brand("Samsung");
        samsung .getCategories().add(memory);
        samsung .getCategories().add(smartphones);
        samsung .getCategories().add(hard_drive);
        Brand savedBrand = brandRepository.save(samsung );

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    public void findAllByName() {
        Iterable<Brand> brands = brandRepository.findAllByName();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    public void testUpdateName(){
        Brand brand = brandRepository.findById(3).get();
        String name= "Samsung Electronics";
        brand.setName(name);

        Brand savedBrand = brandRepository.save(brand);
        assertThat(savedBrand.getName()).isEqualTo(name);
    }
}
