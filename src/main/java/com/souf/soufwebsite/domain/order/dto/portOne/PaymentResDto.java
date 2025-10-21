package com.souf.soufwebsite.domain.order.dto.portOne;

import java.time.LocalDateTime;

public record PaymentResDto(
        String status, // CANCELLED, FAILED, PAID, PAY_PENDING, READY
        String id,
        String transactionId,
        PaymentMethod method,
        LocalDateTime requestedAt,
        PaymentAmount amount,
        SelectedChannel channel

) {
    public record PaymentMethod(
            String type
    ){}
    public record PaymentAmount(
            Long total,
            Long discount,
            Long paid
    ){}
    public record SelectedChannel(
            String pgProvider
    ){}
}
