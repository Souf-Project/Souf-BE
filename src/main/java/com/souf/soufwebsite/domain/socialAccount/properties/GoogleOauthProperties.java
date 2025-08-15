package com.souf.soufwebsite.domain.socialAccount.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
@Data
public class GoogleOauthProperties {
    @Value("${oauth.google.clientId}")
    private String clientId;
    @Value("${oauth.google.clientSecret}")
    private String clientSecret;
    @Value("${oauth.google.redirectUri}")
    private String redirectUri;
    @Value("${oauth.google.tokenUri}")
    private String tokenUri;
    @Value("${oauth.google.userInfoUri}")
    private String userInfoUri;
}
