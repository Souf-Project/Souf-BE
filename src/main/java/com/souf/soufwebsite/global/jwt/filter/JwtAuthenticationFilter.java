package com.souf.soufwebsite.global.jwt.filter;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.report.service.BanService;
import com.souf.soufwebsite.global.jwt.exception.AuthErrorKey;
import com.souf.soufwebsite.global.jwt.exception.BannedAuthenticationException;
import com.souf.soufwebsite.global.jwt.exception.JwtAuthenticationException;
import com.souf.soufwebsite.global.jwt.service.JwtService;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final BanService banService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final Set<String> whitelistPrefixes = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/refresh",
            "/ws",
            "/actuator/health",
            "/favicon.ico",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String uri = request.getRequestURI();
        final boolean isSseSubscribe = uri.startsWith("/api/v1/notifications/subscribe");

        if (isWhitelisted(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 액세스 토큰 검증
        String accessToken = jwtService
                .extractAccessToken(request)
                .orElse(null);

        // SSE 구독 요청이고, 헤더에서 accessToken이 없으면 쿼리 파라미터 token 사용
        if (isSseSubscribe && accessToken == null) {
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && jwtService.isTokenValid(tokenParam)) {
                accessToken = tokenParam;
            }
        }

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtService.validateAccessTokenOrThrow(accessToken);

        log.info("Request URI: {}", request.getRequestURI());

        if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
            throw new JwtAuthenticationException(AuthErrorKey.TOKEN_BLACKLISTED);
        }

        Member member = authenticateUser(accessToken);

        if (banService.isBanned(member.getId())) {
            Duration remaining = banService.remaining(member.getId()).orElse(null);
            throw new BannedAuthenticationException(remaining);
        }


        filterChain.doFilter(request,response);
    }

    // 액세스 토큰으로 사용자 인증 처리
    private Member authenticateUser(String accessToken) {
        String email = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorKey.TOKEN_INVALID));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorKey.MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new JwtAuthenticationException(AuthErrorKey.MEMBER_WITHDRAWN);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(member);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return member;
    }

//    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
//    private String reIssueAccessToken(String refreshToken) {
//        String email = jwtService.extractEmail(refreshToken)
//                .orElseThrow(() -> new IllegalArgumentException("RefreshToken에서 이메일 추출 실패"));
//
//        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);
//        if (refreshToken.equals(storedRefreshToken)) {
//            Member member = memberRepository.findByEmail(email)
//                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다."));
//
//            String newAccessToken = jwtService.createAccessToken(member);
//            log.info("AccessToken 재발급: {}", newAccessToken);
//            return newAccessToken;
//        }
//        throw new IllegalArgumentException("유효하지 않은 refresh token");
//    }

    private boolean isWhitelisted(String uri) {
        return whitelistPrefixes.stream().anyMatch(uri::startsWith);
    }
}