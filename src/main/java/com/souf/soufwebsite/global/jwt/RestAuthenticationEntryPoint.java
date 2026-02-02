package com.souf.soufwebsite.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.global.exception.ExceptionResponse;
import com.souf.soufwebsite.global.jwt.exception.AuthErrorKey;
import com.souf.soufwebsite.global.jwt.exception.BannedAuthenticationException;
import com.souf.soufwebsite.global.jwt.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
            throws IOException {

        if(res.isCommitted())
            return;
        res.resetBuffer();

        AuthErrorKey errorKey = resolveErrorKey(ex);

        if(isSse(req)){
            res.sendError(errorKey.getHttpStatus());
            return;
        }

        res.setStatus(errorKey.getHttpStatus());
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        Object data = null;
        if (ex instanceof BannedAuthenticationException bannedEx) {
            data = new BannedData(
                    bannedEx.getRemaining() == null ? null : bannedEx.getRemaining().getSeconds()
            );
        }

        ExceptionResponse<?> body = ExceptionResponse.fail(
                errorKey.getHttpStatus(),
                errorKey.getMessage(),
                errorKey.name(),
                data
        );

        objectMapper.writeValue(res.getOutputStream(), body);
        res.flushBuffer();
    }

    private boolean isSse(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("text/event-stream");
    }

    private AuthErrorKey resolveErrorKey(AuthenticationException ex) {
        if (ex instanceof JwtAuthenticationException jwtEx) {
            return jwtEx.getErrorKey();
        }
        if (ex instanceof BannedAuthenticationException bannedEx) {
            return bannedEx.getErrorKey();
        }
        return AuthErrorKey.TOKEN_INVALID;
    }

    public record BannedData(Long remainingSeconds) {}
}