package com.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CancelRequest(@NotNull UUID reservationId) {}