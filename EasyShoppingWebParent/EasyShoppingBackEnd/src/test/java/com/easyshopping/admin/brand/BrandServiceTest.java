package com.easyshopping.admin.brand;

import com.easyshopping.admin.brand.repository.BrandRepository;
import com.easyshopping.admin.brand.service.BrandService;
import com.easyshopping.common.entity.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
   private BrandService brandService;

    @BeforeEach
    public void unit(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckUniqueInNewModeReturnDublicatedName(){
        Integer id = null;
        String name = "asus";
        Brand brand = new Brand("asus");

        when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUniqueBrandName(id, name);

        assertThat(result).isEqualTo("DuplicatedBrand");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name =  "acer";

        when(brandRepository.findByName(name)).thenReturn(null);
        String result = brandService.checkUniqueBrandName(id, name);

        assertThat(result).isEqualTo("OK");
    }
}

