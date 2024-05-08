package com.easyshopping.product;

import com.easyshopping.common.entity.product.Product;
import com.easyshopping.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;

    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> listProductByCategory(int pageNumber, Integer categoryId){
        String categoryIdMatch = "-" + String.valueOf(categoryId)+"-";
        Pageable pageable = PageRequest.of(pageNumber - 1,PRODUCTS_PER_PAGE);
        return productRepository.listProductsByCategory(categoryId,categoryIdMatch,pageable);
    }

    public Product getProduct (String alias) throws ProductNotFoundException {
        Product product = productRepository.findProductByAlias(alias);
        if (product==null) {
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }
        return product;
    }

    public Page<Product> search (String keyword, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1,SEARCH_RESULTS_PER_PAGE);
       return productRepository.search(keyword, pageable);
    }

}
