package com.easyshopping.admin.brand.service;

import com.easyshopping.admin.brand.exception.BrandNotFoundException;
import com.easyshopping.admin.brand.repository.BrandRepository;
import com.easyshopping.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandService {
    private BrandRepository brandRepository;

    public static final int BRANDS_PER_PAGE = 5;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> listAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public List<Brand> listAllByName() {
        return brandRepository.findAllByName();
    }

    public Page<Brand> listByPage(Integer pageNum,String sortDirection, String keyword) {
        Sort sort = Sort.by("name");
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
        if (keyword != null) {
            return brandRepository.searchBrands(keyword, pageable);
        }
        return brandRepository.findAll(pageable);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
        brandRepository.deleteById(id);
    }

    public String checkUniqueBrandName(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = brandRepository.findByName(name);
        if (isCreatingNew) {
            if (brandByName != null) {
                return "DuplicatedBrand";
            }
        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "DuplicateBrand";
            }
        }
        return "OK";
    }
}
