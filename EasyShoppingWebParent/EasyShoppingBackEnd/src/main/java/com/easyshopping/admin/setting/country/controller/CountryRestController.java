package com.easyshopping.admin.setting.country.controller;

import com.easyshopping.admin.setting.country.repository.CountryRepository;
import com.easyshopping.common.entity.setting.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryRestController {

    private CountryRepository countryRepository;

    @Autowired
    public CountryRestController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping("/countries/list")
        public List<Country> listAll() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country) {
        Country saveCountry = countryRepository.save(country);
        return String.valueOf(saveCountry.getId());
    }
}
