package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.UserResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.service.MemberService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/auth/signup")
    public SuccessResponse<?> signup(@RequestBody SignupReqDto reqDto) {
        memberService.signup(reqDto);
        return new SuccessResponse<>("회원가입 성공");
    }

    @PostMapping("/auth/login")
    public SuccessResponse<TokenDto> signin(@RequestBody SigninReqDto reqDto) {
        TokenDto tokenDto = memberService.signin(reqDto);
        return new SuccessResponse<>(tokenDto);
    }

    @PatchMapping("/auth/reset/password")
    public SuccessResponse<?> resetPassword(@RequestBody ResetReqDto reqDto) {
        memberService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }

    // 인증번호 전송
    @PostMapping("/auth/email/send")
    public SuccessResponse<Boolean> sendEmailVerification(@RequestParam String email) {
        return new SuccessResponse<>(memberService.sendEmailVerification(email));
    }

    // 인증번호 검증
    @PostMapping("/auth/email/verify")
    public SuccessResponse<Boolean> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        boolean verified = memberService.verifyEmail(email, code);
        return new SuccessResponse<>(verified);
    }

    @PutMapping("/auth/edit")
    public SuccessResponse<?> editUserInfo(@RequestBody ResetReqDto reqDto) {
        memberService.editUserInfo(reqDto);
        return new SuccessResponse<>("회원정보 수정 성공");
    }

    @GetMapping("/member")
    public SuccessResponse<List<UserResDto>> getMembers() {
        return new SuccessResponse<>(memberService.getMembers());
    }

    @GetMapping("/member/{id}")
    public SuccessResponse<UserResDto> getMemberById(@PathVariable Long id) {
        return new SuccessResponse<>(memberService.getMemberById(id));
    }
}
