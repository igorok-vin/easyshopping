package com.easyshopping.admin.product.service;

import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.common.exception.ProductNotFoundException;
import com.easyshopping.admin.product.repository.ProductRepository;
import com.easyshopping.common.entity.product.Product;
import com.easyshopping.common.entity.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Transactional
@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public static final int PRODUCTS_PER_PAGE = 5;

    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Product get(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Could not find any product with ID" + id);
        }
    }

    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }
        product.setUpdatedTime(new Date());
        return productRepository.save(product);
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDatabase = productRepository.findById(productInForm.getId()).get();
        productInDatabase.setCost(productInForm.getCost());
        productInDatabase.setPrice(productInForm.getPrice());
        productInDatabase.setDiscountPercent(productInForm.getDiscountPercent());
        productRepository.save(productInDatabase);
    }

    public String checkUniqueBrandName(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product brandByName = productRepository.findByName(name);
        if (isCreatingNew) {
            if (brandByName != null) {
                return "DuplicatedProduct";
            }
        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "DuplicatedProduct";
            }
        }
        System.out.println("******************************");
        return "OK";
    }

    public void updateProductEnabled(Integer id, boolean enabled) {
        productRepository.updateEnabled(id, enabled);
    }

    public Page<Product> listByPage(int pageNumber, String sortfield, String sortDirection, String keyword, Integer categoryId) {
        Sort sort = Sort.by(sortfield);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE, sort);
        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                return productRepository.findAllInCategoryWithKeyword(categoryId, categoryIdMatch, keyword, pageable);
            }
            return productRepository.findAll(keyword, pageable);
        }
        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
            return productRepository.findAllInCategoryWithoutKeyword(categoryId, categoryIdMatch, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        productRepository.deleteById(id);
    }
/*************************************************/
public static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra image: " + fileName);
                    } catch (IOException e) {
                        LOGGER.error("Could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;
        Set<ProductImage> images = new HashSet<>();
        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String imageName = imageNames[count];
            images.add(new ProductImage(id, imageName, product));
        }
        product.setImagesExtra(images);
    }

    public static void setMainImageName(MultipartFile mainImageName, Product product) {
        if (!mainImageName.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageName.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    public static void setNewExtaImages(MultipartFile[] extaImages, Product product) {
        if (extaImages.length > 0) {
            for (MultipartFile multipartFile : extaImages) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);
            if (id != 0) {
                product.addProductDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addProductDetail(name, value);
            }
        }
    }

    public static void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImagesMultipartFile, Product productSaved) throws IOException {
        if (!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            String uploadDir = "product-images/" + productSaved.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
        }
        if (extraImagesMultipartFile.length > 0) {
            String uploadDir = "product-images/" + productSaved.getId() + "/extras";
            for (MultipartFile multipartFile : extraImagesMultipartFile) {
                if (multipartFile.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }
}
