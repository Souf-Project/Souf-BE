package com.souf.soufwebsite.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.global.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
            throws IOException {

        log.warn("Access Denied for user: {}, URI: {}", req.getRemoteUser(), req.getRequestURI());
        if(res.isCommitted()) {
            return;
        }
        res.resetBuffer();

        if(isSse(req)){
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");

        ExceptionResponse<Void> body = ExceptionResponse.fail(
                403,
                "접근 권한이 없습니다.",
                "FORBIDDEN"
        );

        objectMapper.writeValue(res.getOutputStream(), body);
        res.flushBuffer();
    }

    private boolean isSse(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("text/event-stream");
    }
}