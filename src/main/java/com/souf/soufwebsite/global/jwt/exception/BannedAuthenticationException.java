package com.souf.soufwebsite.global.jwt.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.time.Duration;

@Getter
public class BannedAuthenticationException extends AuthenticationException {

    private final AuthErrorKey errorKey;
    private final Duration remaining;

    public BannedAuthenticationException(Duration remaining) {
        super(AuthErrorKey.MEMBER_BANNED.getMessage());
        this.errorKey = AuthErrorKey.MEMBER_BANNED;
        this.remaining = remaining;
    }

}