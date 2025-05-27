package com.souf.soufwebsite.domain.member.controller.auth;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            @RequestBody @Valid SigninReqDto reqDto
    );

    @Operation(summary = "비밀번호 재설정", description = "입력한 새 비밀번호로 계정 비밀번호를 변경합니다.")
    @PatchMapping("/reset/password")
    SuccessResponse<?> resetPassword(
            @RequestBody @Valid ResetReqDto reqDto
    );

    @Operation(summary = "이메일 인증번호 전송", description = "입력된 이메일로 인증번호를 전송합니다.")
    @PostMapping("/email/send")
    SuccessResponse<Boolean> sendEmailVerification(
            @RequestParam String email
    );

    @Operation(summary = "이메일 인증번호 검증", description = "이메일로 발급된 인증번호와 일치하게 입력하였는지 검증합니다.")
    @PostMapping("/email/verify")
    SuccessResponse<Boolean> verifyEmailCode(
            @RequestParam String email,
            @RequestParam String code
    );

    @Operation(summary = "닉네임 중복 검증", description = "입력된 닉네임이 사용 가능한지 확인합니다.")
    @GetMapping("/nickname/available")
    SuccessResponse<Boolean> isNicknameAvailable(
            @RequestParam @NotEmpty String nickname
    );

}
