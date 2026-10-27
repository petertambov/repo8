package com.service;

import com.config.LimitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final LimitProperties limitProperties;

    public BigDecimal getDefaultLimit() {
        return limitProperties.getDefaultValue();
    }

}