package com.souf.soufwebsite.domain.order.entity;

import com.souf.soufwebsite.domain.order.dto.portOne.PaymentResDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderUuid;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String transactionUuid;

    @Column(nullable = false)
    private Long amount;

    private String pgProvider;

    private String method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private LocalDateTime approvedTime;

    @Builder
    public Payment(String orderUuid, PaymentResDto resDto) {
        this.orderUuid = orderUuid;
        this.paymentId = resDto.id();
        this.transactionUuid = resDto.transactionId();
        this.amount = resDto.amount().paid();
        this.pgProvider = resDto.channel().pgProvider();
        this.method = resDto.method().type();
        this.status = PaymentStatus.PAID;
        this.approvedTime = resDto.requestedAt();
    }

    public static Payment of(String orderUuid, PaymentResDto resDto) {
        return new Payment(orderUuid, resDto);
    }
}
