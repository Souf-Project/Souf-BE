package com.souf.soufwebsite.domain.member.controller.auth;

import com.souf.soufwebsite.domain.member.dto.ReqDto.*;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원가입", description = "회원가입 관련 API")
@RequestMapping("/api/v1/auth")
public interface AuthApiSpecification {

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/signup")
    SuccessResponse<?> signup(
            @RequestBody @Valid SignupReqDto reqDto
    );

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/login")
    SuccessResponse<TokenDto> signin(
            @RequestBody @Valid SigninReqDto reqDto,
            HttpServletResponse response
    );

    @Operation(summary = "비밀번호 재설정", description = "입력한 새 비밀번호로 계정 비밀번호를 변경합니다.")
    @PatchMapping("/reset/password")
    SuccessResponse<?> resetPassword(
            @RequestBody @Valid ResetReqDto reqDto
    );

    @Operation(summary = "이메일 인증번호 전송", description = "회원가입 시 입력된 이메일로 인증번호를 전송합니다.")
    @PostMapping("/signup/email/send")
    SuccessResponse sendSignupEmailVerification(
            @RequestBody @Valid SendEmailReqDto reqDto
    );

    @Operation(summary = "이메일 인증번호 전송", description = "비밀번호 재설정 시 입력된 이메일로 인증번호를 전송합니다.")
    @PostMapping("/reset/email/send")
    SuccessResponse sendResetEmailVerification(
            @RequestBody @Valid SendEmailReqDto reqDto
    );

    @Operation(summary = "이메일 인증번호 검증",
            description = "이메일로 발급된 인증번호와 일치하게 입력하였는지 검증합니다.<br>" +
                    " 인증 목적에 따라 (SIGNUP), (RESET) 또는 (MODIFY)를 입력합니다.")
    @PostMapping("/email/verify")
    SuccessResponse<Boolean> verifyEmailCode(
            @RequestBody @Valid VerifyEmailReqDto reqDto
    );

    @Operation(summary = "닉네임 중복 검증", description = "입력된 닉네임이 사용 가능한지 확인합니다.")
    @GetMapping("/nickname/available")
    SuccessResponse<Boolean> isNicknameAvailable(
            @RequestParam @NotEmpty String nickname
    );

    @Operation(summary = "회원 탈퇴", description = "더이상 서비스를 사용하지 않을 유저가 스프를 탈퇴합니다.")
    @DeleteMapping("/withdraw")
    SuccessResponse<?> withdraw(
            @CurrentEmail String email,
            @RequestBody @Valid WithdrawReqDto reqDto);

}
