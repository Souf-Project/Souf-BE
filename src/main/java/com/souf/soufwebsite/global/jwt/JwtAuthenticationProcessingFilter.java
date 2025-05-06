package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
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

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_URL = "/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals(LOGIN_URL) || request.getRequestURI().equals(LOGOUT_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 리프레시 토큰이 있는 경우 -> 새 액세스 토큰 발급
        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        // 액세스 토큰 검증
        String accessToken = jwtService
                .extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (accessToken != null && refreshToken != null) {
            authenticateUser(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null && refreshToken != null) {
            reIssueAccessToken(response, refreshToken);
            return;
        }

        if (accessToken != null) {
            // Redis 블랙리스트 확인 (로그아웃된 토큰인지 검사)
            if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("로그아웃된 토큰입니다.");
                return;
            }
            // 정상적인 토큰이면 인증 정보 저장
            authenticateUser(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    // 액세스 토큰으로 사용자 인증 처리
    private void authenticateUser(String accessToken) {
        jwtService.extractEmail(accessToken).ifPresent(
                email -> memberRepository.findByEmail(email).ifPresent(
                        user -> {
                            UserDetailsImpl userDetails = new UserDetailsImpl(user);
                            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);
                            SecurityContextHolder.setContext(context);
                        }
                )
        );
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
    private void reIssueAccessToken(HttpServletResponse response, String refreshToken) {
        String email = redisTemplate.opsForValue().get("refresh:" + refreshToken);
        String newAccessToken = jwtService.createAccessToken(email);
        jwtService.sendAccessToken(response, newAccessToken);
        log.info("AccessToken 재발급: {}", newAccessToken);
    }
}