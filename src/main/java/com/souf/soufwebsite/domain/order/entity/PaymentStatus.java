package com.souf.soufwebsite.domain.order.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    PENDING,
    PAID,
    REFUNDED,
    FAILED,
    PAID_ESCROW_HOLD // 거래 완료 전 보관 상태를 의미
}
