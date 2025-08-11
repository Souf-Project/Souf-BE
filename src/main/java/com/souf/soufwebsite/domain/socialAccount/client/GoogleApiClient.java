package com.souf.soufwebsite.domain.socialAccount.client;

import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialUserInfo;
import com.souf.soufwebsite.domain.socialAccount.dto.google.GoogleUserResDto;
import com.souf.soufwebsite.domain.socialAccount.dto.google.GoogleTokenResDto;
import com.souf.soufwebsite.domain.socialAccount.properties.GoogleOauthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GoogleApiClient implements SocialApiClient {

    private final WebClient webClient = WebClient.builder().build();
    private final GoogleOauthProperties googleOauthProperties;

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.GOOGLE;
    }

    @Override
    public SocialUserInfo getUserInfoByCode(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    private String getAccessToken(String code) {
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code" +
                        "&client_id=" + googleOauthProperties.getClientId() +
                        "&client_secret=" + googleOauthProperties.getClientSecret() +
                        "&redirect_uri=" + googleOauthProperties.getRedirectUri() +
                        "&code=" + code)
                .retrieve()
                .bodyToMono(GoogleTokenResDto.class)
                .map(GoogleTokenResDto::accessToken)
                .block();
    }

    private SocialUserInfo getUserInfo(String accessToken) {
        GoogleUserResDto response = webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserResDto.class)
                .block();

        return new SocialUserInfo(
                response.id(),
                response.email(),
                response.name(),
                response.picture()
        );
    }
}