package com.easyshopping.site.product;

import com.easyshopping.common.entity.product.Product;
import com.easyshopping.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void testFindByAlias(){
        Product product = productRepository.findProductByAlias("Acer-Aspire-TC-885-UA92-Desktop");

        assertThat(product).isNotNull();
    }
}
