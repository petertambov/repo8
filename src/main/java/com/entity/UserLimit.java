package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "user_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLimit {

    @Id
    private Long userId;

    @Column(name = "limit_amount", nullable = false)
    private BigDecimal limitAmount;
}