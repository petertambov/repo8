package com.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ReserveRequest(
        @NotNull Long userId,
        @NotNull @Positive(message = "Сумма должна быть положительной") BigDecimal amount,
        String operationId
) {
}