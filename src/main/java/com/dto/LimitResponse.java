package com.dto;

import java.math.BigDecimal;

public record LimitResponse(Long userId, BigDecimal limit) {}