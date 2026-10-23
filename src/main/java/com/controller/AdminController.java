package com.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import com.entity.Reservation;
import com.entity.ReservationStatus;
import com.repository.ReservationRepository;
import com.service.SettingService;

@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final SettingService settingService;
    private final ReservationRepository reservationRepository;

    public AdminController(SettingService settingService, ReservationRepository reservationRepository) {
        this.settingService = settingService;
        this.reservationRepository = reservationRepository;
    }

    @PutMapping("/settings/default-limit")
    public void setDefaultLimit(@RequestParam @Positive(message = "Лимит должен быть положительным") BigDecimal limit) {
        settingService.setDefaultLimit(limit);
    }

    @GetMapping("/reservations/status/{status}")
    public List<Reservation> getReservationsByStatus(@PathVariable ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
}