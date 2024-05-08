package com.easyshopping.admin.setting.state;

import com.easyshopping.common.entity.setting.Country;
import com.easyshopping.common.entity.setting.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	public List<State> findByCountryOrderByNameAsc(Country country);
}
