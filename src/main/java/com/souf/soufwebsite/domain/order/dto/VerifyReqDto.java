package com.souf.soufwebsite.domain.order.dto;

public record VerifyReqDto(
        String orderId,
        String paymentId
) {
}
