package com.souf.soufwebsite.domain.oauth.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.oauth.SocialProvider;
import com.souf.soufwebsite.domain.oauth.client.GoogleApiClient;
import com.souf.soufwebsite.domain.oauth.client.KakaoApiClient;
import com.souf.soufwebsite.domain.oauth.dto.LoginResDto;
import com.souf.soufwebsite.domain.oauth.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.oauth.dto.SocialMemberInfo;
import com.souf.soufwebsite.global.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final KakaoApiClient kakaoApiClient;
    //    private final NaverApiClient naverApiClient;
    private final GoogleApiClient googleApiClient;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public LoginResDto loginOrSignUp(SocialLoginReqDto request) {
        SocialMemberInfo memberInfo = switch (request.provider()) {
            case KAKAO -> kakaoApiClient.getMemberInfoByCode(request.code());
//            case NAVER -> naverApiClient.getMemberInfoByCode(request.getCode());
            case GOOGLE -> googleApiClient.getMemberInfoByCode(request.code());
            default -> throw new IllegalStateException("Unexpected value: " + request.provider());
        };

        Member member = memberRepository.findBySocialIdAndSocialProvider(memberInfo.socialId(), request.provider())
                .orElseGet(() -> registerMember(memberInfo, request.provider()));

        String jwt = jwtService.issueToken(member);
        boolean isNewMember = member.getCreatedTime().isAfter(LocalDateTime.now().minusMinutes(1));

        return new LoginResDto(jwt, isNewMember);
    }

    private Member registerMember(SocialMemberInfo info, SocialProvider provider) {
        Member member = new Member(
                provider,
                info.socialId(),
                info.email(),
                info.nickname(),
                info.profileImageUrl()
        );

        return memberRepository.save(member);
    }
}