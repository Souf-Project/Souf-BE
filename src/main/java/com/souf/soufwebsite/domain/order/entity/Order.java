package com.souf.soufwebsite.domain.order.entity;

import com.souf.soufwebsite.domain.order.dto.OrderReqDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderUuid;

    private Long clientId;
    private Long freelancerId;

    @Column(nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Builder
    public Order(String orderUuid, Long clientId, Long freelancerId, Long totalAmount) {
        this.orderUuid = orderUuid;
        this.clientId = clientId;
        this.freelancerId = freelancerId;
        this.totalAmount = totalAmount;
    }

    public static Order of(String orderUuid, OrderReqDto reqDto) {
        return Order.builder()
                .orderUuid(orderUuid)
                .clientId(reqDto.clientId())
                .freelancerId(reqDto.freelancerId())
                .totalAmount(reqDto.totalAmount()).build();
    }

    public void updateOrderStatus(OrderStatus status) {
        this.status = status;
    }
}
