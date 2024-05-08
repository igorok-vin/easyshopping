package com.easyshopping.admin.setting.general.repository;

import com.easyshopping.common.entity.setting.general.Currency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
    @Query("SELECT c from Currency c ORDER BY c.name ASC")
    public List<Currency>findAllByNameInAscOrder();
}
