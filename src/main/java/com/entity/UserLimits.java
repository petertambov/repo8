package com.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserLimits {

    @Id
    private Long userId;

    @Column(nullable = false)
    private BigDecimal limitAmount;

    public UserLimits(Long userId, BigDecimal limitAmount) {
        this.userId = userId;
        this.limitAmount = limitAmount;
    }
}