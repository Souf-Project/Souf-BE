package com.souf.soufwebsite.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING,
    IN_PROGRESS,
    DONE,
    CANCELLED
}
