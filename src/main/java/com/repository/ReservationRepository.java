package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

import com.entity.Reservations;
import com.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservations, UUID> {

    List<Reservations> findByStatus(ReservationStatus status);

    @Modifying
    @Query("UPDATE Reservations r SET r.status = :newStatus WHERE r.status = :oldStatus")
    int updateStatusAll(@Param("oldStatus") ReservationStatus oldStatus,
                        @Param("newStatus") ReservationStatus newStatus);
}