package com.souf.soufwebsite.domain.socialAccount.service;

import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import com.souf.soufwebsite.domain.socialAccount.client.SocialApiClient;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialUserInfo;
import com.souf.soufwebsite.domain.socialAccount.entity.SocialAccount;
import com.souf.soufwebsite.domain.socialAccount.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.socialAccount.repository.SocialAccountRepository;
import com.souf.soufwebsite.global.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.UUID.randomUUID;

@Service
public class SocialAccountService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtService jwtService;

    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private final Map<SocialProvider, SocialApiClient> clientMap;

    public SocialAccountService(
            List<SocialApiClient> clients,
            MemberRepository memberRepository,
            SocialAccountRepository socialAccountRepository,
            JwtService jwtService,
            RedisTemplate<String, String> redisTemplate,
            PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;

        EnumMap<SocialProvider, SocialApiClient> map = new EnumMap<>(SocialProvider.class);
        for (SocialApiClient c : clients) {
            map.put(c.getProvider(), c);
        }
        this.clientMap = map;
    }


    @Transactional
    public TokenDto loginOrSignUp(SocialLoginReqDto request,  HttpServletResponse response) {
        SocialApiClient client = clientMap.get(request.provider());
        if (client == null) {
            throw new NotValidAuthenticationException();
        }

        SocialUserInfo info = client.getUserInfoByCode(request.code());

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
                        .displayName(firstNonBlank(info.name(), ""))
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

    private Member resolveMemberForSocial(SocialUserInfo info) {
        if (info.email() != null) {
            return memberRepository.findByEmail(info.email())
                    .orElseGet(() -> createMemberFromSocial(info));
        }
        return createMemberFromSocial(info);
    }

    private Member createMemberFromSocial(SocialUserInfo info) {
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

    // null
    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}