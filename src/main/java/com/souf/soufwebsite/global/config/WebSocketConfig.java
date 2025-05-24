package com.souf.soufwebsite.global.config;

import com.souf.soufwebsite.global.jwt.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Value("${websocket.relay.host}")
    private String relayHost;
    @Value("${websocket.relay.port}")
    private int relayPort;
    @Value("${websocket.relay.username}")
    private String relayUsername;
    @Value("${websocket.relay.password}")
    private String relayPassword;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // 발행 prefix
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setClientLogin(relayUsername)
                .setClientPasscode(relayPassword);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 웹소켓 연결 엔드포인트
                .setAllowedOriginPatterns("*");
//                .withSockJS(); // SockJS 지원
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // STOMP 인증 처리 인터셉터 등록
    }
}

