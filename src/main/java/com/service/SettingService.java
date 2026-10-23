package com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

import com.entity.Setting;
import com.repository.SettingRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingService {

    private final SettingRepository settingRepository;

    @Value("${app.default-limit}")
    private BigDecimal defaultLimitFromConfig;

    @PostConstruct
    public void initDefaultSetting() {
        if (!settingRepository.existsById("default_limit")) {
            Setting setting = new Setting("default_limit", defaultLimitFromConfig.toString());
            settingRepository.save(setting);
            log.info("Лимит по умолчанию из конфигурации приложения: {}", defaultLimitFromConfig);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getDefaultLimit() {
        Setting setting = settingRepository.findById("default_limit")
                .orElseThrow(() -> new IllegalStateException("Лимит по умолчанию не установлен"));
        return new BigDecimal(setting.getValue());
    }

    @Transactional
    public void setDefaultLimit(BigDecimal limit) {
        Setting setting = settingRepository.findById("default_limit")
                .orElse(new Setting("default_limit", limit.toString()));
        setting.setValue(limit.toString());
        settingRepository.save(setting);
    }
}