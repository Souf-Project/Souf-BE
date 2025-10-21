package com.souf.soufwebsite.domain.order.dto;

import com.souf.soufwebsite.domain.order.entity.Order;

public record OrderResDto(
        String orderId,
        Long totalAmount
) {
    public static OrderResDto from(Order order) {
        return new OrderResDto(order.getOrderUuid(), order.getTotalAmount());
    }
}
