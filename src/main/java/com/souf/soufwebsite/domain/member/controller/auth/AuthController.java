package com.souf.soufwebsite.domain.member.controller.auth;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.service.MemberService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController implements AuthApiSpecification{
    private final MemberService memberService;

    @PostMapping("/signup")
    public SuccessResponse<?> signup(@RequestBody @Valid SignupReqDto reqDto) {
        memberService.signup(reqDto);
        return new SuccessResponse<>("회원가입 성공");
    }

    @PostMapping("/login")
    public SuccessResponse<TokenDto> signin(@RequestBody @Valid SigninReqDto reqDto) {
        log.info("로그인 요청: {}", reqDto);
        TokenDto tokenDto = memberService.signin(reqDto);
        log.info("로그인 성공: {}", tokenDto);
        return new SuccessResponse<>(tokenDto);
    }

    @PatchMapping("/reset/password")
    public SuccessResponse<?> resetPassword(@RequestBody ResetReqDto reqDto) {
        memberService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }

    // 인증번호 전송
    @PostMapping("/email/send")
    public SuccessResponse<Boolean> sendEmailVerification(@RequestParam String email) {
        return new SuccessResponse<>(memberService.sendEmailVerification(email));
    }

    // 인증번호 검증
    @PostMapping("/email/verify")
    public SuccessResponse<Boolean> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        boolean verified = memberService.verifyEmail(email, code);
        return new SuccessResponse<>(verified);
    }

    // 이메일 중복 검증
    @GetMapping("/nickname/available")
    public SuccessResponse<Boolean> isNicknameAvailable(
            @RequestParam @NotEmpty String nickname) {
        boolean available = memberService.isNicknameAvailable(nickname);
        return new SuccessResponse<>(available, "닉네임 사용 가능 여부");
    }
}
