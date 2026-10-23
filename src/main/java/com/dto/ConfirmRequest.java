package com.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ConfirmRequest(@NotNull UUID reservationId) {}