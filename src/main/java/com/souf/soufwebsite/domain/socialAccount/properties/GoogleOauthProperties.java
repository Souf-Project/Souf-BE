package com.souf.soufwebsite.domain.socialAccount.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
@Data
public class GoogleOauthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
