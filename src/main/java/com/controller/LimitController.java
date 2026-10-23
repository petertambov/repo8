package com.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import com.dto.*;
import com.entity.Reservation;
import com.service.LimitService;

@RestController
@RequestMapping("/api/v1/limits")
@Validated
public class LimitController {

    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingUserId() {
        return new ErrorResponse("MISSING_USER_ID", "Идентификатор пользователя обязателен");
    }

    @GetMapping("/{userId}")
    public LimitResponse getLimit(@PathVariable @Positive Long userId) {
        BigDecimal limit = limitService.getLimit(userId);
        return new LimitResponse(userId, limit);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(@Valid @RequestBody ReserveRequest request) {
        Reservation reservation = limitService.reserve(request);
        return new ReservationResponse(reservation.getId());
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirm(@Valid @RequestBody ConfirmRequest request) {
        limitService.confirm(request.reservationId());
    }

    @PostMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancel(@Valid @RequestBody CancelRequest request) {
        limitService.cancel(request.reservationId());
    }

    @PostMapping("/rollback")
    @ResponseStatus(HttpStatus.OK)
    public void rollback(@Valid @RequestBody RollbackRequest request) {
        limitService.rollback(request.reservationId());
    }
}