package com.easyshopping.admin.setting;

import com.easyshopping.admin.setting.general.repository.SettingRepository;
import com.easyshopping.common.entity.setting.general.Setting;
import com.easyshopping.common.entity.setting.general.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepositoryTest {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testCreateGeneral1Setting() {
        Setting siteName = new Setting("SITE_NAME","Easy Shopping", SettingCategory.GENERAL);
        Setting savedResult = settingRepository.save(siteName);

        assertThat(savedResult).isNotNull();
    }

    @Test
    public void testCreateGeneral2Setting() {
        Setting siteLogo = new Setting("SITE_LOGO","EasyShopping.png", SettingCategory.GENERAL);
        Setting copyRight = new Setting("COPYRIGHT","Copyright (C) 2024 Easy Shopping Ltd.", SettingCategory.GENERAL);
        Iterable<Setting> savedResult = settingRepository.saveAll(List.of(siteLogo,copyRight));

        assertThat(savedResult).size().isGreaterThan(0);
    }

    @Test
    public void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID","1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL","$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION","before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE","POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS","2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE","COMMA", SettingCategory.CURRENCY);

        Iterable<Setting> savedResult = settingRepository.saveAll(List.of(currencyId,symbol,symbolPosition, decimalPointType,decimalDigits,thousandsPointType));

        assertThat(savedResult).size().isGreaterThan(0);
    }

    @Test
    public void testListSettingsByCategory() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.GENERAL);
        settings.forEach(System.out::println);

    }
}
