package com.souf.soufwebsite.domain.member.controller.auth;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.*;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberUpdateResDto;
import com.souf.soufwebsite.domain.member.service.general.MemberService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.servlet.http.HttpServletResponse;
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
    public SuccessResponse<MemberUpdateResDto> signup(@RequestBody @Valid SignupReqDto reqDto) {
        MemberUpdateResDto result = memberService.signup(reqDto);
        return new SuccessResponse<>(result, "회원가입 성공");
    }

    @PostMapping("/signup/upload")
    public SuccessResponse<?> uploadAuthenticationMetadata(@Valid @RequestBody MediaReqDto mediaReqDto) {
        memberService.uploadAuthenticationImage(mediaReqDto);

        return new SuccessResponse<>("인증 파일이 성공적으로 업로드되었습니다!");
    }

    @PostMapping("/login")
    public SuccessResponse<TokenDto> signin(@RequestBody @Valid SigninReqDto reqDto, HttpServletResponse response) {
        //log.info("로그인 요청: {}", reqDto);
        TokenDto tokenDto = memberService.signin(reqDto, response);
        //log.info("로그인 성공: {}", tokenDto);
        return new SuccessResponse<>(tokenDto);
    }

    @PatchMapping("/reset/password")
    public SuccessResponse<?> resetPassword(@RequestBody @Valid ResetReqDto reqDto) {
        memberService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }

    // 인증번호 전송
    @PostMapping("/signup/email/send")
    public SuccessResponse sendSignupEmailVerification(@RequestBody SendEmailReqDto reqDto) {

        memberService.sendSignupEmailVerification(reqDto);

        return new SuccessResponse<>("인증번호가 전송되었습니다.");
    }

    @PostMapping("/reset/email/send")
    public SuccessResponse sendResetEmailVerification(@RequestBody SendEmailReqDto reqDto) {

        memberService.sendResetEmailVerification(reqDto);

        return new SuccessResponse<>("비밀번호 재설정 인증번호가 전송되었습니다.");
    }

    // 인증번호 검증
    @PostMapping("/email/verify")
    public SuccessResponse<Boolean> verifyEmailCode(@RequestBody @Valid VerifyEmailReqDto reqDto) {
        boolean verified = memberService.verifyEmail(reqDto);
        return new SuccessResponse<>(verified);
    }


    // 이메일 중복 검증
    @GetMapping("/nickname/available")
    public SuccessResponse<Boolean> isNicknameAvailable(
            @RequestParam @NotEmpty String nickname) {
        boolean available = memberService.isNicknameAvailable(nickname);
        return new SuccessResponse<>(available, "닉네임 사용 가능 여부");
    }

    @DeleteMapping("/withdraw")
    public SuccessResponse<?> withdraw(
            @CurrentEmail String email,
            @RequestBody @Valid WithdrawReqDto reqDto) {
        memberService.withdraw(email, reqDto);
        return new SuccessResponse<>("회원 탈퇴 성공");
    }
}
