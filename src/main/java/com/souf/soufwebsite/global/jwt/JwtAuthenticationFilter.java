package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.member.repository.MemberRepository;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final Set<String> whitelistPrefixes = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/refresh",
            "/ws",
            "/actuator/health"
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

        if(accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if(!jwtService.isTokenValid(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("EXPIRED OR INVALID TOKEN");
            return;
        }

        log.info("Request URI: {}", request.getRequestURI());
//        log.info("AccessToken: {}", accessToken);
//        log.info("RefreshToken: {}", refreshToken);

        if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("this token is in blacklist");
            return;
        }

        authenticateUser(accessToken);
        filterChain.doFilter(request,response);
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