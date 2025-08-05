package com.souf.soufwebsite.global.config;

import com.souf.soufwebsite.domain.oauth.properties.GoogleOauthProperties;
import com.souf.soufwebsite.domain.oauth.properties.KakaoOauthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KakaoOauthProperties.class, GoogleOauthProperties.class})
public class OauthConfig {
}