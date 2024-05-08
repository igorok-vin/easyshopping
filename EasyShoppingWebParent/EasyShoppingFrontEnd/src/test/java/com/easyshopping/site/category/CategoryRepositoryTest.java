package com.easyshopping.site.category;

import com.easyshopping.category.CategoryRepository;
import com.easyshopping.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testListEnabled(){
        List<Category> findAllEnabled = categoryRepository.findAllByEnabledEqualsTrue();

      findAllEnabled.forEach(category -> {
          System.out.println(category.getName() + "(" + category.isEnabled()+")");
      });
    }

    @Test
    public void testFindCategoryByAlias(){
        Category findCategoryByAlias = categoryRepository.finByAliasEnabled("computers");

        assertThat(findCategoryByAlias).isNotNull();
    }
}
