package com;

import com.config.LimitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(LimitProperties.class)
public class PaymentServiceLimitsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceLimitsApplication.class, args);
    }
}