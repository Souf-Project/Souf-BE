package com.souf.soufwebsite.domain.socialAccount.service;

import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.NotAgreedPersonalInfoException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.opensearch.EntityType;
import com.souf.soufwebsite.domain.opensearch.OperationType;
import com.souf.soufwebsite.domain.opensearch.event.IndexEventPublisherHelper;
import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import com.souf.soufwebsite.domain.socialAccount.client.SocialApiClient;
import com.souf.soufwebsite.domain.socialAccount.dto.*;
import com.souf.soufwebsite.domain.socialAccount.entity.SocialAccount;
import com.souf.soufwebsite.domain.socialAccount.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.socialAccount.repository.SocialAccountRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.jwt.JwtService;
import com.souf.soufwebsite.global.slack.service.SlackService;
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
    private final CategoryService categoryService;
    private final JwtService jwtService;
    private final IndexEventPublisherHelper indexEventPublisherHelper;
    private final SlackService slackService;


    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private final Map<SocialProvider, SocialApiClient> clientMap;

    public SocialAccountService(
            List<SocialApiClient> clients,
            MemberRepository memberRepository,
            SocialAccountRepository socialAccountRepository,
            CategoryService categoryService,
            JwtService jwtService,
            IndexEventPublisherHelper indexEventPublisherHelper,
            SlackService slackService,
            RedisTemplate<String, String> redisTemplate,
            PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.categoryService = categoryService;
        this.jwtService = jwtService;
        this.indexEventPublisherHelper = indexEventPublisherHelper;
        this.slackService = slackService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;

        EnumMap<SocialProvider, SocialApiClient> map = new EnumMap<>(SocialProvider.class);
        for (SocialApiClient c : clients) {
            map.put(c.getProvider(), c);
        }
        this.clientMap = map;
    }


    @Transactional(readOnly = true)
    public SocialLoginResDto loginOrSignUp(SocialLoginReqDto request) {
        SocialApiClient client = clientMap.get(request.provider());
        if (client == null) throw new NotValidAuthenticationException();

        SocialUserInfo info = client.getUserInfoByCode(request.code());

        // 1) 기존 소셜 연결이 있으면 → 바로 로그인
        SocialAccount account = socialAccountRepository
                .findByProviderAndProviderUserId(request.provider(), info.socialId())
                .orElse(null);

        if (account != null) {
            Member member = account.getMember();
            TokenDto token = issueTokens(member); // 아래 헬퍼 참고
            return new SocialLoginResDto(false, token, null,
                    new SocialPrefill(member.getEmail(), member.getUsername(),
                            info.profileImageUrl(), request.provider().name()));
        }

        // 2) 연결이 없으면 → 온보딩 필요 (DB 생성 금지!)
        String registrationToken = java.util.UUID.randomUUID().toString();

        // Redis 등에 임시 세션 저장 (TTL 10분 예시)
        // 값에는 provider/socialId/email/profile 등 최소 식별/프리필용만 저장
        var payload = Map.of(
                "provider", request.provider().name(),
                "socialId", info.socialId(),
                "email", info.email(),
                "name", info.name(),
                "profileImageUrl", info.profileImageUrl()
        );
        redisTemplate.opsForHash().putAll("social:reg:" + registrationToken, payload);
        redisTemplate.expire("social:reg:" + registrationToken, java.time.Duration.ofMinutes(10));

        return new SocialLoginResDto(
                true,                      // requiresSignup
                null,                      // token 없음
                registrationToken,         // 프론트가 들고 온보딩 완료 호출에 사용
                new SocialPrefill(info.email(), info.name(),
                        info.profileImageUrl(), request.provider().name())
        );
    }


    @Transactional
    public TokenDto completeSignup(SocialCompleteSignupReqDto reqDto, HttpServletResponse response) {
        String key = "social:reg:" + reqDto.registrationToken();
        if (!redisTemplate.hasKey(key)) {
            throw new IllegalStateException("registrationToken expired or invalid");
        }

        // Redis에서 소셜 식별 정보 로드
        String providerName = (String) redisTemplate.opsForHash().get(key, "provider");
        String socialId     = (String) redisTemplate.opsForHash().get(key, "socialId");
        String email        = (String) redisTemplate.opsForHash().get(key, "email");
        String name         = (String) redisTemplate.opsForHash().get(key, "name");
        String profileImage = (String) redisTemplate.opsForHash().get(key, "profileImageUrl");

        SocialProvider provider = SocialProvider.valueOf(providerName);

        // 안전장치: 혹시 그사이(다른 탭) 연결이 생겼다면 바로 로그인 처리
        SocialAccount existing = socialAccountRepository
                .findByProviderAndProviderUserId(provider, socialId)
                .orElse(null);
        if (existing != null) {
            TokenDto token = issueTokens(existing.getMember());
            jwtService.sendAccessAndRefreshToken(response, token.accessToken(), // 필요 시
                    redisTemplate.opsForValue().get("refresh:" + existing.getMember().getEmail()));
            redisTemplate.delete(key);
            return token;
        }

        // 개인 정보 동의 확인
        if (reqDto.isPersonalInfoAgreed().equals(Boolean.FALSE)) {
            throw new NotAgreedPersonalInfoException();
        }

        // 1) Member 생성 (닉네임/카테고리 반영)
        Member member = new Member(
                email,
                passwordEncoder.encode("SOCIAL@" + java.util.UUID.randomUUID()),
                name != null && !name.isBlank() ? name : "user_" + java.util.UUID.randomUUID().toString().substring(0,6),
                reqDto.nickname(),
                RoleType.MEMBER,
                reqDto.isMarketingAgreed()
        );

        injectCategories(reqDto.categoryDtos(), member);

        memberRepository.save(member);

        // 2) SocialAccount 연결
        socialAccountRepository.save(SocialAccount.builder()
                .provider(provider)
                .providerUserId(socialId)
                .member(member)
                .providerEmail(email)
                .displayName(name)
                .profileImageUrl(profileImage)
                .build());

        // 3) 토큰 발급/전송
        TokenDto token = issueTokens(member);
        jwtService.sendAccessAndRefreshToken(response, token.accessToken(),
                redisTemplate.opsForValue().get("refresh:" + member.getEmail()));

        indexEventPublisherHelper.publishIndexEvent(
                EntityType.MEMBER,
                OperationType.CREATE,
                "Member",
                member
        );

        redisTemplate.delete(key);
        slackService.sendSlackMessage(member.getNickname() + " 님이 회원가입했습니다.", "signup");

        return token;
    }

    private TokenDto issueTokens(Member member) {
        String accessToken = jwtService.createAccessToken(member);
        String refreshToken = jwtService.createRefreshToken(member);

        redisTemplate.opsForValue().set(
                "refresh:" + member.getEmail(),
                refreshToken,
                jwtService.getExpiration(refreshToken),
                TimeUnit.MILLISECONDS
        );

        return TokenDto.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .nickname(member.getNickname())
                .roleType(member.getRole())
                .build();
    }

    private void injectCategories(List<CategoryDto> categoryDtos, Member member) {
        for (CategoryDto dto : categoryDtos) {
            FirstCategory first = categoryService.findIfFirstIdExists(dto.firstCategory());
            SecondCategory second = categoryService.findIfSecondIdExists(dto.secondCategory());
            ThirdCategory third = categoryService.findIfThirdIdExists(dto.thirdCategory());
            categoryService.validate(dto.firstCategory(), dto.secondCategory(), dto.thirdCategory());

            MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
            member.addCategory(mapping);
        }
    }
}