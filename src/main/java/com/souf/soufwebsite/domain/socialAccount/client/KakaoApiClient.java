package com.souf.soufwebsite.domain.socialAccount.client;

import com.souf.soufwebsite.domain.socialAccount.dto.SocialUserInfo;
import com.souf.soufwebsite.domain.socialAccount.dto.kakao.KakaoUserResDto;
import com.souf.soufwebsite.domain.socialAccount.dto.kakao.KakaoTokenResDto;
import com.souf.soufwebsite.domain.socialAccount.properties.KakaoOauthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements SocialApiClient {
    private final WebClient webClient;
    private final KakaoOauthProperties kakaoOauthProperties;

    public SocialUserInfo getUserInfoByCode(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    private String getAccessToken(String code) {
        return webClient.post()
                .uri(kakaoOauthProperties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "grant_type=authorization_code" +
                                "&client_id=" + kakaoOauthProperties.getClientId() +
                                "&redirect_uri=" + kakaoOauthProperties.getRedirectUri() +
                                "&code=" + code
                )
                .retrieve()
                .bodyToMono(KakaoTokenResDto.class)
                .map(KakaoTokenResDto::accessToken)
                .block(); // 단일 호출이므로 동기 처리
    }

    private SocialUserInfo getUserInfo(String accessToken) {
        KakaoUserResDto kakaoUser = webClient.get()
                .uri(kakaoOauthProperties.getUserInfoUri())
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .retrieve()
                .bodyToMono(KakaoUserResDto.class)
                .block();

        return new SocialUserInfo(
                String.valueOf(kakaoUser.id()),
                kakaoUser.kakaoAccount().email(),
                kakaoUser.kakaoAccount().profile().nickname(),
                kakaoUser.kakaoAccount().profile().profileImageUrl()
        );
    }
}