package com.souf.soufwebsite.domain.order.entity;

import lombok.Getter;

@Getter
public enum PayoutStatus {

    REQUESTED,
    PAID,
    FAILED
}
