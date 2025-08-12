package com.souf.soufwebsite.domain.socialAccount.controller;

import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.socialAccount.service.SocialAccountService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/social")
public class SocialAccountController implements SocialAccountApiSpecification{

    private final SocialAccountService socialAccountService;

    @PostMapping("/login")
    public SuccessResponse<TokenDto> login(@RequestBody @Valid SocialLoginReqDto request, HttpServletResponse response) {
        TokenDto tokenDto = socialAccountService.loginOrSignUp(request, response);
        return new SuccessResponse<>(tokenDto);
    }
}