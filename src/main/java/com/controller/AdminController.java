package com.controller;

import com.config.LimitProperties;
import com.service.ReservationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import com.entity.ReservationStatus;
import com.dto.ReservationsDTO;

@RestController
@RequestMapping("/admin")
@Validated
@RequiredArgsConstructor
public class AdminController {

    private final ReservationsService reservationsService;
    private final LimitProperties limitProperties;

    @GetMapping("/settings/default-limit")
    public BigDecimal getDefaultLimit() {
         return limitProperties.getDefaultValue();
    }

    @GetMapping("/reservations/status/{status}")
    public List<ReservationsDTO> getReservationsDTOByStatus(@PathVariable ReservationStatus status) {
        return reservationsService.findByStatus(status);
    }
}