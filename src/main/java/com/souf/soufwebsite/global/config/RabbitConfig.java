package com.souf.soufwebsite.global.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "notifications.exchange";
    public static final String QUEUE_NAME = "notifications.queue";
    public static final String ROUTING_KEY = "notifications.routing.key";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding notificationBinding(DirectExchange exchange, Queue notificationQueue) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(exchange)
                .with(ROUTING_KEY);
    }
}
