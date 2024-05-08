package com.easyshopping.admin.brand.repository;

import com.easyshopping.common.entity.Brand;
import com.easyshopping.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer>{

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    public Page<Brand> searchBrands(String keyword, Pageable pageable);

    Long countById(Integer id);

    public Brand findByName(String name);

    @Query("SELECT NEW Brand (b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    public List<Brand> findAllByName();
}
