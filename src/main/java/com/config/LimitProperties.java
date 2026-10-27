package com.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app.limits")
@Data
public class LimitProperties {
    private BigDecimal defaultValue;
}