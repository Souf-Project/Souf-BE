package com.souf.soufwebsite.domain.notification.service;

import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.global.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationDto notificationDto) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                notificationDto
        );
    }
}
