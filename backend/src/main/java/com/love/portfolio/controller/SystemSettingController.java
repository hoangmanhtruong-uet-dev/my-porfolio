package com.love.portfolio.controller;

import com.love.portfolio.model.SystemSetting;
import com.love.portfolio.repository.SystemSettingRepository;
import com.love.portfolio.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SystemSettingController {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/{key}")
    public SystemSetting getSetting(@PathVariable String key) {
        return systemSettingRepository.findById(key)
                .orElseGet(() -> {
                    SystemSetting defaultSetting = new SystemSetting();
                    defaultSetting.setSettingKey(key);
                    defaultSetting.setSettingValue("");
                    return defaultSetting;
                });
    }

    @PostMapping("/{key}")
    public SystemSetting updateSetting(@PathVariable String key, @RequestParam("value") String value) {
        SystemSetting setting = systemSettingRepository.findById(key)
                .orElse(new SystemSetting());
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        return systemSettingRepository.save(setting);
    }

    @PostMapping("/{key}/upload")
    public SystemSetting uploadSettingImage(
            @PathVariable String key,
            @RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(file, "settings");
        SystemSetting setting = systemSettingRepository.findById(key)
                .orElse(new SystemSetting());
        setting.setSettingKey(key);
        setting.setSettingValue(imageUrl);
        return systemSettingRepository.save(setting);
    }
}
