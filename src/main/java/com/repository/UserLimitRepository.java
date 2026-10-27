package com.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

import com.entity.UserLimits;

public interface UserLimitRepository extends JpaRepository<UserLimits, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ul FROM UserLimits ul WHERE ul.userId = :userId")
    Optional<UserLimits> findByIdWithLock(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserLimits ul SET ul.limitAmount = :limit")
    int updateAllLimits(@Param("limit") BigDecimal limit);
}