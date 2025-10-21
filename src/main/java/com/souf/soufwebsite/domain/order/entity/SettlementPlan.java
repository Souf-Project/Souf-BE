package com.souf.soufwebsite.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settlement_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderUuid;

    private Double advanceRate;

    private Double platformFeeRate;

    private Boolean withholdTax = Boolean.TRUE; // 세금 적용
}
