package com.dto;

import com.entity.ReservationStatus; // Enum можно передавать как есть
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationsDTO(
        UUID id,
        Long userId,
        BigDecimal amount,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }