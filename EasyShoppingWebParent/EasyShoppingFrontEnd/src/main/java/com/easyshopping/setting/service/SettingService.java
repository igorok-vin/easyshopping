package com.easyshopping.setting.service;

import com.easyshopping.common.entity.setting.general.Setting;
import com.easyshopping.common.entity.setting.general.SettingCategory;
import com.easyshopping.setting.repository.CurrencyRepository;
import com.easyshopping.setting.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {
    private SettingRepository settingRepository;
    private CurrencyRepository currencyRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository, CurrencyRepository currencyRepository) {
        this.settingRepository = settingRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<Setting> getGeneralSettings() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }
}
