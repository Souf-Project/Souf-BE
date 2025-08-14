package com.souf.soufwebsite.domain.socialAccount.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
@Data
public class KakaoOauthProperties {
    @Value("${oauth.kakao.clientId}")
    private String clientId;
    @Value("${oauth.kakao.redirectUri}")
    private String redirectUri;
    @Value("${oauth.kakao.tokenUri}")
    private String tokenUri;
    @Value("${oauth.kakao.userInfoUri}")
    private String userInfoUri;
}
