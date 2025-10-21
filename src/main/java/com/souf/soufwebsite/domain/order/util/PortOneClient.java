package com.souf.soufwebsite.domain.order.util;

import com.souf.soufwebsite.domain.order.dto.portOne.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PortOneClient {

    private final WebClient webClient;

    public Mono<PaymentResDto> getPayment(String paymentId){

        return webClient.get()
                .uri("/payments/{paymentId}", paymentId)
                .retrieve()
                .bodyToMono(PaymentResDto.class);
    }

    public Mono<PartnersResDto> createPartner(PartnersReqDto reqDto) {

        return webClient.post()
                .uri("/platform/partners")
                .bodyValue(reqDto)
                .retrieve()
                .bodyToMono(PartnersResDto.class);
    }

    public Mono<PayoutResDto> createPayout(PayoutReqDto reqDto){

        return webClient.post()
                .uri("/platform/transfers/order")
                .bodyValue(reqDto)
                .retrieve()
                .bodyToMono(PayoutResDto.class);
    }
}
