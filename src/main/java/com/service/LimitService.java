package com.service;

import com.entity.Reservations;
import com.entity.UserLimits;
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
import com.entity.Reservations;
import com.entity.ReservationStatus;
import com.entity.UserLimits;
import com.exception.MyException;
import com.repository.ReservationRepository;
import com.repository.UserLimitRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {

    private final UserLimitRepository userLimitRepository;
    private final ReservationRepository reservationRepository;
    private final SettingsService settingService;

    @Transactional
    public BigDecimal getLimit(Long userId) {
        return getOrCreateUserLimit(userId).getLimitAmount();
    }

    @Transactional
    public UserLimits createUserLimit(Long userId) {
        BigDecimal defaultLimit = settingService.getDefaultLimit();
        UserLimits userLimits = new UserLimits(userId, defaultLimit);
        return userLimitRepository.save(userLimits);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserLimits getOrCreateUserLimit(Long userId) {
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
    public Reservations reserve(ReserveRequest request) {
        Long userId = request.userId();
        BigDecimal amount = request.amount();

        UserLimits userLimits = getOrCreateUserLimit(userId);

        if (userLimits.getLimitAmount().compareTo(amount) < 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "Недостаточный лимит для пользователя: " + userId);
        }

        userLimits.setLimitAmount(userLimits.getLimitAmount().subtract(amount));
        userLimitRepository.save(userLimits);

        Reservations reservation = new Reservations(
                userId,
                amount,
                ReservationStatus.RESERVED
        );
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void confirm(UUID reservationId) {
        Reservations reservations = findReservation(reservationId);
        if (reservations.getStatus() != ReservationStatus.RESERVED) {
            throw new MyException(HttpStatus.BAD_REQUEST,
                    "Бронирование " + reservationId + " не ЗАРЕЗЕРВИРОВАНО");
        }
        reservations.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservations);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancel(UUID reservationId) {
        Reservations reservations = findReservation(reservationId);
        if (reservations.getStatus() != ReservationStatus.RESERVED) {
            throw new MyException(HttpStatus.BAD_REQUEST,
                    "Бронирование " + reservationId + " не ЗАРЕЗЕРВИРОВАНО");
        }

        UserLimits userLimits = userLimitRepository.findByIdWithLock(reservations.getUserId())
                .orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Пользовательский лимит не найден"));
        userLimits.setLimitAmount(userLimits.getLimitAmount().add(reservations.getAmount()));
        userLimitRepository.save(userLimits);

        reservations.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservations);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rollback(UUID reservationId) {
        Reservations reservations = findReservation(reservationId);
        if (reservations.getStatus() != ReservationStatus.CONFIRMED) {
            throw new MyException(HttpStatus.BAD_REQUEST,
                    "Бронирование " + reservationId + " не ПОДТВЕРЖДЕНО");
        }

        UserLimits userLimits = userLimitRepository.findByIdWithLock(reservations.getUserId())
                .orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Пользовательский лимит не найден"));
        userLimits.setLimitAmount(userLimits.getLimitAmount().add(reservations.getAmount()));
        userLimitRepository.save(userLimits);

        reservations.setStatus(ReservationStatus.ROLLED_BACK);
        reservationRepository.save(reservations);
    }

    private Reservations findReservation(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Бронирование не найдено: " + id));
    }
}
