package com.souf.soufwebsite.domain.user.controller;

import com.souf.soufwebsite.domain.user.dto.EditUserRequest;
import com.souf.soufwebsite.domain.user.dto.ReqDto.EditReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.SignUpRequest;
import com.souf.soufwebsite.domain.user.dto.TokenDto;
import com.souf.soufwebsite.domain.user.service.UserService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public SuccessResponse<?> signup(@RequestBody SignUpRequest reqDto) {
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

    @PostMapping("/auth/logout")
    public SuccessResponse<?> logout(@RequestHeader("Authorization") String accessToken) {
        userService.logout(accessToken);
        return new SuccessResponse<>("로그아웃 성공");
    }

    @PostMapping("/auth/reissue")
    public SuccessResponse<TokenDto> reissue(@RequestHeader("Refresh") String refreshToken) {
        TokenDto newToken = userService.reissue(refreshToken);
        return new SuccessResponse<>(newToken);
    }

    @PatchMapping("/auth/reset/password")
    public SuccessResponse<?> resetPassword(@RequestBody EditReqDto reqDto) {
        userService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }

    @GetMapping("/auth/school")
    public SuccessResponse<Boolean> checkSchoolVerification(@RequestParam String schoolName) {
        boolean verified = userService.checkSchoolVerification(schoolName);
        return new SuccessResponse<>(verified);
    }

    @PostMapping("/auth/authentication/email")
    public SuccessResponse<Boolean> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean verified = userService.verifyEmail(email, code);
        return new SuccessResponse<>(verified);
    }

    @PutMapping("/auth/edit")
    public SuccessResponse<?> editUserInfo(@RequestBody EditReqDto reqDto) {
        userService.editUserInfo(reqDto);
        return new SuccessResponse<>("회원정보 수정 성공");
    }

    @GetMapping("/member")
    public SuccessResponse<?> getMembers() {
        return new SuccessResponse<>(userService.getMembers());
    }

    @GetMapping("/member/{id}")
    public SuccessResponse<?> getMemberById(@PathVariable Long id) {
        return new SuccessResponse<>(userService.getMemberById(id));
    }
}
