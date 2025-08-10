package com.souf.soufwebsite.domain.socialAccount.controller;

import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.socialAccount.service.SocialAccountService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
public class SocialAccountController {

    private final SocialAccountService socialAccountService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody SocialLoginReqDto request, HttpServletResponse response) {
        TokenDto resDto = socialAccountService.loginOrSignUp(request, response);
        return ResponseEntity.ok(resDto);
    }
}