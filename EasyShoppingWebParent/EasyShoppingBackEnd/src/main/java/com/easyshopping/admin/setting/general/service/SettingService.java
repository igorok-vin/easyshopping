package com.easyshopping.admin.setting.general.service;

import com.easyshopping.admin.config.FileUploadUtil;
import com.easyshopping.admin.setting.general.repository.CurrencyRepository;
import com.easyshopping.admin.setting.general.repository.SettingRepository;
import com.easyshopping.common.entity.setting.general.Currency;
import com.easyshopping.common.entity.setting.general.GeneralSettingBag;
import com.easyshopping.common.entity.setting.general.Setting;
import com.easyshopping.common.entity.setting.general.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SettingService {
    private SettingRepository settingRepository;
    private CurrencyRepository currencyRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository, CurrencyRepository currencyRepository) {
        this.settingRepository = settingRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<Setting> findAll() {
        return (List<Setting>) settingRepository.findAll();
    }

    public GeneralSettingBag getGeneralSettings() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> deneralSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        settings.addAll(deneralSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);
    }

    public void savedAll (Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    public void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "site-logo/" + fileName;
            settingBag.updateSiteLogo(value);
            String uploadDir = "site-logo";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    public void saveCurrencySymbol(HttpServletRequest httpServletRequest, GeneralSettingBag settingBag) {
        Integer currencyId = Integer.parseInt(httpServletRequest.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);
        if(findByIdResult.isPresent()){
            Currency currency = findByIdResult.get();
            String symbol = currency.getSymbol();
            settingBag.updateCurrencySymbol(symbol);
        }
    }

    public void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settingList) {
        for (Setting setting: settingList) {
            String value = request.getParameter(setting.getKey());
            if(value !=null) {
                setting.setValue(value);
            }
        }
        settingList.forEach(System.out::println);
        savedAll(settingList);
    }
}
