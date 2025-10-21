package com.souf.soufwebsite.domain.order.dto;

public record OrderReqDto(
        Long clientId,
        Long freelancerId,
        Long totalAmount
) {
}
