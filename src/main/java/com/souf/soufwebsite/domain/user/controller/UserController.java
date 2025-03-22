package com.souf.soufwebsite.domain.user.controller;

import com.souf.soufwebsite.domain.user.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.user.dto.ResDto.UserResDto;
import com.souf.soufwebsite.domain.user.dto.TokenDto;
import com.souf.soufwebsite.domain.user.service.UserService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public SuccessResponse<?> signup(@RequestBody SignupReqDto reqDto) {
        userService.signup(reqDto);
        return new SuccessResponse<>("회원가입 성공");
    }

    @PostMapping("/auth/login")
    public SuccessResponse<TokenDto> signin(@RequestBody SigninReqDto reqDto, HttpServletResponse response) {
        TokenDto tokenDto = userService.signin(reqDto);
        response.setHeader("Authorization", "Bearer " + tokenDto.accessToken());
        response.setHeader("Refresh", "Bearer " + tokenDto.refreshToken());
        return new SuccessResponse<>(tokenDto);
    }

    @PatchMapping("/auth/reset/password")
    public SuccessResponse<?> resetPassword(@RequestBody ResetReqDto reqDto) {
        userService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }

    // 인증번호 전송
    @PostMapping("/auth/email/send")
    public SuccessResponse<Boolean> sendEmailVerification(@RequestParam String email) {
        boolean sent = userService.sendEmailVerification(email);
        return new SuccessResponse<>(sent);
    }

    // 인증번호 검증
    @PostMapping("/auth/email/verify")
    public SuccessResponse<Boolean> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        boolean verified = userService.verifyEmail(email, code);
        return new SuccessResponse<>(verified);
    }

    @PutMapping("/auth/edit")
    public SuccessResponse<?> editUserInfo(@RequestBody ResetReqDto reqDto) {
        userService.editUserInfo(reqDto);
        return new SuccessResponse<>("회원정보 수정 성공");
    }

    @GetMapping("/member")
    public SuccessResponse<List<UserResDto>> getMembers() {
        return new SuccessResponse<>(userService.getMembers());
    }

    @GetMapping("/member/{id}")
    public SuccessResponse<UserResDto> getMemberById(@PathVariable Long id) {
        return new SuccessResponse<>(userService.getMemberById(id));
    }
}
