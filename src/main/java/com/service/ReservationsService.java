package com.service;

import com.dto.ReservationsDTO;
import com.entity.ReservationStatus;
import com.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationsService {

    private final ReservationRepository reservationRepository;

    public List<ReservationsDTO> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status)
                .stream()
                .map(entity -> new ReservationsDTO(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getAmount(),
                        entity.getStatus(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ))
                .toList();
    }
}

