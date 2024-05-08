package com.easyshopping.admin.setting.country.repository;

import com.easyshopping.common.entity.setting.Country;
import com.easyshopping.common.entity.setting.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer> {

    public List<Country>findAllByOrderByNameAsc();
}
