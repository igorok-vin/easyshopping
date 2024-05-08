package com.easyshopping.admin.setting.general.repository;

import com.easyshopping.common.entity.setting.general.Setting;
import com.easyshopping.common.entity.setting.general.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository <Setting, String> {

    public List<Setting> findByCategory(SettingCategory category);

}
