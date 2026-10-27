package com.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import com.dto.*;
import com.service.LimitService;

@RestController
@RequestMapping("/api/v1/limits")
@Validated
@RequiredArgsConstructor
public class LimitController {

    private final LimitService limitService;

    @GetMapping("/{userId}")
    public LimitResponse getLimit(@PathVariable @Positive Long userId) {
        BigDecimal limit = limitService.getLimit(userId);
        return new LimitResponse(userId, limit);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(@Valid @RequestBody ReserveRequest request) {
        return new ReservationResponse(limitService.reserve(request).getId());
    }

    @PostMapping("/confirm")
    public void confirm(@Valid @RequestBody ConfirmRequest request) {
        limitService.confirm(request.reservationId());
    }

    @PostMapping("/cancel")
    public void cancel(@Valid @RequestBody CancelRequest request) {
        limitService.cancel(request.reservationId());
    }

    @PostMapping("/rollback")
    public void rollback(@Valid @RequestBody RollbackRequest request) {
        limitService.rollback(request.reservationId());
    }
}