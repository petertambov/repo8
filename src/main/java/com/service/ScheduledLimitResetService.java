package com.service;

import com.config.LimitProperties;
import com.entity.Reservations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

import com.entity.ReservationStatus;
import com.repository.ReservationRepository;
import com.repository.UserLimitRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledLimitResetService {

    private final UserLimitRepository userLimitRepository;
    private final ReservationRepository reservationRepository;
    private final LimitProperties limitProperties;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetLimits() {

        List<Reservations> activeReservations = reservationRepository.findByStatus(ReservationStatus.RESERVED);
        log.info("Найдено активных резерваций перед сбросом: {}", activeReservations.size());

        BigDecimal defaultLimit = limitProperties.getDefaultValue();
        log.info("Сброс всех лимитов по умолчанию: {}", defaultLimit);

        int updatedRows = userLimitRepository.updateAllLimits(defaultLimit);
        log.info("Обновлено лимитов: {}", updatedRows);

        int updated = reservationRepository.updateStatusAll(
                ReservationStatus.RESERVED,
                ReservationStatus.CANCELLED
        );
        log.info("Все незавершённые резервации {} становятся недействительными", updated);

    }
}