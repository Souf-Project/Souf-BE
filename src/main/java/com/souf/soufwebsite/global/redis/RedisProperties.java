package com.souf.soufwebsite.global.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisProperties {

    private int port;
    private String host;
    private String password;
}
