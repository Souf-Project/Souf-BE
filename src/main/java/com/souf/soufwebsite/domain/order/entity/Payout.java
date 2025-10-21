package com.souf.soufwebsite.domain.order.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payout extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderUuid;

    @Column(nullable = false)
    private Long freelancerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutType payoutType;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status = PayoutStatus.REQUESTED;

    private Long amount;

    private Long platformFee;
    private Long taxWithholding;

    private String payoutUuid;

    private LocalDateTime paidTime;
}
