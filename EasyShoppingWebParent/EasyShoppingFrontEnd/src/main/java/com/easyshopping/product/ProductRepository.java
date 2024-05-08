package com.easyshopping.product;

import com.easyshopping.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    @Query("select p from Product p where p.enabled=true and (p.category.id=?1 or p.category.allParentIDs like %?2%) order by p.name ASC ")
    public Page<Product> listProductsByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);

    public Product findProductByAlias(String alias);

    @Query(value = "select * from products where enabled=true and match(name,short_description, full_description) against (?1)",
            nativeQuery = true,
            countQuery = "SELECT COUNT(p.id) FROM products p where enabled=true and match(name,short_description, full_description) against (?1)")
    public Page<Product> search(String keyword, Pageable pageable);
}
