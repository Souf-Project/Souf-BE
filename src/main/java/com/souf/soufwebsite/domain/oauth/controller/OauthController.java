package com.souf.soufwebsite.domain.oauth.controller;

import com.souf.soufwebsite.domain.oauth.dto.LoginResDto;
import com.souf.soufwebsite.domain.oauth.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.oauth.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody SocialLoginReqDto request) {
        LoginResDto response = oauthService.loginOrSignUp(request);
        return ResponseEntity.ok(response);
    }
}