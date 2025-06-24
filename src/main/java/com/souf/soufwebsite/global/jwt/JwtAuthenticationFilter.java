package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOGIN_URL = "/api/v1/auth/login";
    private static final String LOGOUT_URL = "/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {



        if (request.getRequestURI().startsWith("/ws") || request.getRequestURI().startsWith(LOGIN_URL) || request.getRequestURI().equals(LOGOUT_URL)) {
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

        log.info("Request URI: {}", request.getRequestURI());
        log.info("AccessToken: {}", accessToken);
        log.info("RefreshToken: {}", refreshToken);


        if (accessToken != null && refreshToken != null) {
            if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("this token is expired token");
                return;
            }

            authenticateUser(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null && refreshToken != null) {
            String newAccessToken = reIssueAccessToken(refreshToken);
            jwtService.sendAccessToken(response, newAccessToken);

            // 응답 종료. 인증이나 체인 호출 없음 (프론트가 다음 요청을 새 토큰으로 해야 함)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token re-issued. Please retry with new token.");
            return;
        }

        if (accessToken != null) {
            // Redis 블랙리스트 확인 (로그아웃된 토큰인지 검사)
            if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("this token is expired token");
                return;
            }
            // 정상적인 토큰이면 인증 정보 저장
            authenticateUser(accessToken);
        }


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

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
    private String reIssueAccessToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken에서 이메일 추출 실패"));

        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);
        if (refreshToken.equals(storedRefreshToken)) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다."));

            String newAccessToken = jwtService.createAccessToken(member);
            log.info("AccessToken 재발급: {}", newAccessToken);
            return newAccessToken;
        }

        throw new IllegalArgumentException("유효하지 않은 refresh token");
    }
}