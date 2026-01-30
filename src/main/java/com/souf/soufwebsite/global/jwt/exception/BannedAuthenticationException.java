package com.souf.soufwebsite.global.jwt.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.time.Duration;

@Getter
public class BannedAuthenticationException extends AuthenticationException {

    private final Duration remaining;

    public BannedAuthenticationException(Duration remaining) {
        super(AuthErrorKey.MEMBER_BANNED.getMessage());
        this.remaining = remaining;
    }

    public AuthErrorKey getErrorKey() {
        return AuthErrorKey.MEMBER_BANNED;
    }
}