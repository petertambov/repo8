package com.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

import com.entity.UserLimit;

public interface UserLimitRepository extends JpaRepository<UserLimit, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ul FROM UserLimit ul WHERE ul.userId = :userId")
    Optional<UserLimit> findByIdWithLock(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserLimit ul SET ul.limitAmount = :limit")
    int updateAllLimits(@Param("limit") BigDecimal limit);
}