package com.easyshopping.admin.product;

import com.easyshopping.admin.product.repository.ProductRepository;
import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import com.easyshopping.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void testCreateProduct1(){
        Brand brand = testEntityManager.find(Brand.class,10);
        Category category = testEntityManager.find(Category.class,15);

        Product product = new Product();
        product.setName("Samsung Galaxy A31");
        product.setAlias("samsung_galaxy_a31");
        product.setShortDescription("A good smartphone from Samsung");
        product.setFullDescription("This is a very good smartphone full description");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(456);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateProduct2(){
        Brand brand = testEntityManager.find(Brand.class,40);
        Category category = testEntityManager.find(Category.class,6);

        Product product = new Product();
        product.setName("Asus Dragon KF45");
        product.setAlias("asus_dragon_kf45");
        product.setShortDescription("A good laptop from Asus");
        product.setFullDescription("This is a very good laptop full description");
        product.setBrand(brand);
        product.setCategory(category);
        product.setCost(1480);
        product.setPrice(1850);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateProduct3(){
        Brand brand = testEntityManager.find(Brand.class,37);
        Category category = testEntityManager.find(Category.class,5);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("A good laptop from Acer");
        product.setFullDescription("This is a very good desktop full description");
        product.setBrand(brand);
        product.setCategory(category);
        product.setCost(1350);
        product.setPrice(1780);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> iterable = productRepository.findAll();
        iterable.forEach(System.out::println);
    }

    @Test
    public void testGetProduct(){
        Optional<Product> byIdProduct = productRepository.findById(2);
        System.out.println(byIdProduct.get());
        assertThat(byIdProduct.get()).isNotNull();
    }
    @Test
    public void testUpdateProduct(){
        Integer id = 2;
        Product product = productRepository.findById(id).get();
        product.setPrice(1780);
        productRepository.save(product);
        Product updatedProduct = testEntityManager.find(Product.class, id);
        assertThat(updatedProduct.getPrice()).isEqualTo(1780);
    }

    @Test
    public void testSaveProductWithImages(){
        Integer productId = 1;

        Product product = productRepository.findById(productId).get();
        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra image 2.png");
        product.addExtraImage("extra image 3.png");

        Product saveProduct = productRepository.save(product);
        assertThat(saveProduct.getImagesExtra().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails(){

    }
}
