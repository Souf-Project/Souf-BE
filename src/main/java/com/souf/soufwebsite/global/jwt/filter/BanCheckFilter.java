package com.souf.soufwebsite.global.jwt.filter;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.report.service.BanService;
import com.souf.soufwebsite.global.jwt.exception.BannedAuthenticationException;
import com.souf.soufwebsite.global.util.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class BanCheckFilter extends OncePerRequestFilter {

    private final BanService banService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Member member = SecurityUtils.getCurrentMemberOrNull();
        if (member != null && banService.isBanned(member.getId())) {
            Duration remaining = banService.remaining(member.getId()).orElse(null);
            throw new BannedAuthenticationException(remaining);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/v1/normal/check");
    }
}
