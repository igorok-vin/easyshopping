package com.easyshopping.admin.setting.general.controller;

import com.easyshopping.admin.setting.general.repository.CurrencyRepository;
import com.easyshopping.admin.setting.general.service.SettingService;
import com.easyshopping.common.entity.setting.general.Currency;
import com.easyshopping.common.entity.setting.general.GeneralSettingBag;
import com.easyshopping.common.entity.setting.general.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class SettingController {

    private SettingService settingService;
    private CurrencyRepository currencyRepository;

    @Autowired
    public SettingController(SettingService settingService, CurrencyRepository currencyRepository) {
        this.settingService = settingService;
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Currency> listCurrencies = currencyRepository.findAllByNameInAscOrder();

        List<Setting> listSettings = settingService.findAll();
        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        model.addAttribute("listCurrencies", listCurrencies);
        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {

        GeneralSettingBag settingBag = settingService.getGeneralSettings();

        settingService.saveSiteLogo(multipartFile, settingBag);
        settingService.saveCurrencySymbol(request, settingBag);
        settingService.updateSettingValuesFromForm(request, settingBag.list());

        redirectAttributes.addFlashAttribute("message", "General settings have been saved");
        return "redirect:/settings";

    }


}
