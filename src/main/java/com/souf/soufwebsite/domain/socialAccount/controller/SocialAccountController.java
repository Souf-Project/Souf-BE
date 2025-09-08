package com.souf.soufwebsite.domain.socialAccount.controller;

import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialCompleteSignupReqDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLinkReqDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginReqDto;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialLoginResDto;
import com.souf.soufwebsite.domain.socialAccount.service.SocialAccountService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/social")
public class SocialAccountController implements SocialAccountApiSpecification{

    private final SocialAccountService socialAccountService;

    @PostMapping("/login")
    public SuccessResponse<SocialLoginResDto> login(@RequestBody @Valid SocialLoginReqDto req) {
        return new SuccessResponse<>(socialAccountService.loginOrSignUp(req));
    }

    @PostMapping("/complete-signup")
    public SuccessResponse<TokenDto> completeSignup(@RequestBody @Valid SocialCompleteSignupReqDto req,
                                                    HttpServletResponse res) {
        return new SuccessResponse<>(socialAccountService.completeSignup(req, res));
    }

    @PostMapping("/link")
    public SuccessResponse<Map<String, Object>> link(
            @RequestBody @Valid SocialLinkReqDto req
    ) {
        Long userId = SecurityUtils.getCurrentMember().getId();
        socialAccountService.linkForLoggedIn(userId, req);
        return new SuccessResponse<>(Map.of(
                "linked", true,
                "provider", req.provider().name()
        ));
    }
}