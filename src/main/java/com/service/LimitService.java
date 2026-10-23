package com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.dto.ReserveRequest;
import com.entity.Reservation;
import com.entity.ReservationStatus;
import com.entity.UserLimit;
import com.exception.MyException;
import com.repository.ReservationRepository;
import com.repository.UserLimitRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {

    private final UserLimitRepository userLimitRepository;
    private final ReservationRepository reservationRepository;
    private final SettingService settingService;

    @Transactional
    public BigDecimal getLimit(Long userId) {
        return getOrCreateUserLimit(userId).getLimitAmount();
    }

    @Transactional
    public UserLimit createUserLimit(Long userId) {
        BigDecimal defaultLimit = settingService.getDefaultLimit();
        UserLimit userLimit = new UserLimit(userId, defaultLimit);
        return userLimitRepository.save(userLimit);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserLimit getOrCreateUserLimit(Long userId) {
        return userLimitRepository.findByIdWithLock(userId)
                .orElseGet(() -> {
                    try {
                        return createUserLimit(userId);
                    } catch (DataIntegrityViolationException e) {
                        return userLimitRepository.findByIdWithLock(userId)
                                .orElseThrow(() -> new MyException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Не удалось создать лимит для пользователя " + userId
                                ));
                    }
                });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Reservation reserve(ReserveRequest request) {
        Long userId = request.userId();
        BigDecimal amount = request.amount();

        UserLimit userLimit = getOrCreateUserLimit(userId);

        if (userLimit.getLimitAmount().compareTo(amount) < 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "Недостаточный лимит для пользователя: " + userId);
        }

        userLimit.setLimitAmount(userLimit.getLimitAmount().subtract(amount));
        userLimitRepository.save(userLimit);

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .amount(amount)
                .operationId(request.operationId())
                .status(ReservationStatus.RESERVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void confirm(UUID reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new MyException(HttpStatus.BAD_REQUEST,
                    "Бронирование " + reservationId + " не ЗАРЕЗЕРВИРОВАНО");
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancel(UUID reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new MyException(HttpStatus.BAD_REQUEST,
                    "Бронирование " + reservationId + " не ЗАРЕЗЕРВИРОВАНО");
        }

        UserLimit userLimit = userLimitRepository.findByIdWithLock(reservation.getUserId())
                .orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Пользовательский лимит не найден"));
        userLimit.setLimitAmount(userLimit.getLimitAmount().add(reservation.getAmount()));
        userLimitRepository.save(userLimit);

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rollback(UUID reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new MyException(HttpStatus.BAD_REQUEST,
                    "Бронирование " + reservationId + " не ПОДТВЕРЖДЕНО");
        }

        UserLimit userLimit = userLimitRepository.findByIdWithLock(reservation.getUserId())
                .orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Пользовательский лимит не найден"));
        userLimit.setLimitAmount(userLimit.getLimitAmount().add(reservation.getAmount()));
        userLimitRepository.save(userLimit);

        reservation.setStatus(ReservationStatus.ROLLED_BACK);
        reservationRepository.save(reservation);
    }

    private Reservation findReservation(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Бронирование не найдено: " + id));
    }
}
