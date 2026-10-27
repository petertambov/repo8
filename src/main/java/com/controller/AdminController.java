package com.controller;

import com.config.LimitProperties;
import com.service.ReservationsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import com.entity.ReservationStatus;
import com.dto.ReservationsDTO;

@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final ReservationsService reservationsService;
    private final LimitProperties limitProperties;

    public AdminController(ReservationsService reservationsService,
                           LimitProperties limitProperties) {
        this.reservationsService = reservationsService;
        this.limitProperties = limitProperties;
    }

    @GetMapping("/settings/default-limit")
    public BigDecimal getDefaultLimit() {
         return limitProperties.getDefaultValue();
    }

    @GetMapping("/reservations/status/{status}")
    public List<ReservationsDTO> getReservationsDTOByStatus(@PathVariable ReservationStatus status) {
        return reservationsService.findByStatus(status);
    }
}