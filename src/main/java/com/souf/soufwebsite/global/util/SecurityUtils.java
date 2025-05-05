package com.souf.soufwebsite.global.util;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.exception.AuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
public class SecurityUtils {
    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current user: {}", authentication.getPrincipal());
        log.info("Current username: {}", authentication.getName());

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizedException();
        }

        Object principal = authentication.getPrincipal();

        return (Member) principal;
    }
}
