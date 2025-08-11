package com.souf.soufwebsite.domain.socialAccount.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.socialAccount.client.GoogleApiClient;
import com.souf.soufwebsite.domain.socialAccount.client.KakaoApiClient;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialMemberInfo;
import com.souf.soufwebsite.domain.socialAccount.entity.SocialAccount;
import com.souf.soufwebsite.domain.socialAccount.repository.SocialAccountRepository;
import com.souf.soufwebsite.global.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class SocialAccountService {

    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;
    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtService jwtService;

    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate; // (1) 주입
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder; // (1) 주입

    @Transactional
    public TokenDto loginOrSignUp(SocialLoginReqDto request,  HttpServletResponse response) { // (2)
        SocialMemberInfo info = switch (request.provider()) {
            case KAKAO -> kakaoApiClient.getMemberInfoByCode(request.code());
            case GOOGLE -> googleApiClient.getMemberInfoByCode(request.code());
            default -> throw new IllegalArgumentException("Unsupported provider");
        };

        SocialAccount account = socialAccountRepository
                .findByProviderAndProviderUserId(request.provider(), info.socialId())
                .orElse(null);

        Member member;
        if (account != null) {
            member = account.getMember();
        } else {
            member = resolveMemberForSocial(info);
            try {
                socialAccountRepository.save(SocialAccount.builder()
                        .provider(request.provider())
                        .providerUserId(info.socialId())
                        .member(member)
                        .providerEmail(info.email())
                        .displayName(info.name())
                        .profileImageUrl(info.profileImageUrl())
                        .build());
            } catch (DataIntegrityViolationException e) {
                // 동시 요청 등으로 유니크 충돌 시 재조회
                member = socialAccountRepository.findByProviderAndProviderUserId(request.provider(), info.socialId())
                        .map(SocialAccount::getMember)
                        .orElseThrow(() -> e);
            }
        }

        String accessToken = jwtService.createAccessToken(member);
        String refreshToken = jwtService.createRefreshToken(member);
        redisTemplate.opsForValue().set("refresh:" + member.getEmail(),
                refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .nickname(member.getNickname())
                .roleType(member.getRole())
                .build();
    }

    private Member resolveMemberForSocial(SocialMemberInfo info) {
        if (info.email() != null) {
            return memberRepository.findByEmail(info.email())
                    .orElseGet(() -> createMemberFromSocial(info));
        }
        return createMemberFromSocial(info);
    }

    private Member createMemberFromSocial(SocialMemberInfo info) {
        // 닉네임을 나중에 설정한다면 임시 닉네임 사용
        String tmpNickname = "user_" + randomUUID().toString().substring(0, 8);
        String randomPassword = passwordEncoder.encode("SOCIAL@" + randomUUID());

        Member member = Member.builder()
                .email(info.email())
                .password(randomPassword)
                .username(info.name())
                .nickname(tmpNickname)
                .role(RoleType.MEMBER)
                .build();
        return memberRepository.save(member);
    }
}