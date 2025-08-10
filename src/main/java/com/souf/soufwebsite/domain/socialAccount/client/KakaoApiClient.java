package com.souf.soufwebsite.domain.socialAccount.client;

import com.souf.soufwebsite.domain.socialAccount.dto.SocialMemberInfo;
import com.souf.soufwebsite.domain.socialAccount.dto.kakao.KakaoMemberResDto;
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

    public SocialMemberInfo getMemberInfoByCode(String code) {
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

    private SocialMemberInfo getUserInfo(String accessToken) {
        KakaoMemberResDto kakaoUser = webClient.get()
                .uri(kakaoOauthProperties.getUserInfoUri())
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .retrieve()
                .bodyToMono(KakaoMemberResDto.class)
                .block();

        return new SocialMemberInfo(
                String.valueOf(kakaoUser.id()),
                kakaoUser.kakaoAccount().email(),
                kakaoUser.kakaoAccount().profile().nickname(),
                kakaoUser.kakaoAccount().profile().profileImageUrl()
        );
    }
}