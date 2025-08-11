package com.souf.soufwebsite.domain.socialAccount.controller;

import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "SocialLogin", description = "소셜로그인 관련 API")
public interface SocialAccountApiSpecification {

    @Operation(summary = "소셜 로그인", description = "소셜 로그인을 통해 사용자를 인증하고 토큰을 발급합니다.")
    @PostMapping("/login")
    ResponseEntity<TokenDto> login(@RequestBody SocialLoginReqDto request, HttpServletResponse response);
}
