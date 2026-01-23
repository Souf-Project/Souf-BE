package com.souf.soufwebsite.global.jwt.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final AuthErrorKey errorKey;

    public JwtAuthenticationException(AuthErrorKey errorKey) {
        super(errorKey.getMessage());
        this.errorKey = errorKey;
    }
}