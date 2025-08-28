package com.souf.soufwebsite.global.util;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.exception.AuthorizedException;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
public class SecurityUtils {
    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizedException(); // 인증되지 않은 사용자
        }

        Object principal = authentication.getPrincipal();

        // 예외 방지: principal이 Member인 경우에만 반환
        if (principal instanceof Member member) {
            return member;
        }

        // principal이 UserDetailsImpl 같은 커스텀 클래스인 경우
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getMember();
        }

        // 그 외에는 예외 처리
        throw new AuthorizedException();
    }

    public static Member getCurrentMemberOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Member m) return m;
        if (principal instanceof UserDetailsImpl u) return u.getMember();
        return null;
    }
}
