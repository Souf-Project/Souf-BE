package com.souf.soufwebsite.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.global.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
            throws IOException {

        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json;charset=UTF-8");

        ExceptionResponse<Void> body = ExceptionResponse.fail(
                403,
                "접근 권한이 없습니다.",
                "FORBIDDEN"
        );

        res.getWriter().write(objectMapper.writeValueAsString(body));
    }
}