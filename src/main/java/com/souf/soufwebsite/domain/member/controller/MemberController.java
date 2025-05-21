package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.service.MemberService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/auth/signup")
    public SuccessResponse<?> signup(@RequestBody @Valid SignupReqDto reqDto) {
        memberService.signup(reqDto);
        return new SuccessResponse<>("회원가입 성공");
    }

    @PostMapping("/auth/login")
    public SuccessResponse<TokenDto> signin(@RequestBody @Valid SigninReqDto reqDto) {
        log.info("로그인 요청: {}", reqDto);
        TokenDto tokenDto = memberService.signin(reqDto);
        log.info("로그인 성공: {}", tokenDto);
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

    @PutMapping("/auth/update")
    public SuccessResponse<?> updateUserInfo(@RequestBody UpdateReqDto reqDto) {
        memberService.updateUserInfo(reqDto);
        return new SuccessResponse<>("회원정보 수정 성공");
    }

    @GetMapping("/member")
    public SuccessResponse<List<MemberResDto>> getMembers(
            @RequestParam(name = "search") String search
    ) {
        return new SuccessResponse<>(memberService.getMembers());
    }

    @GetMapping("/member/{id}")
    public SuccessResponse<MemberResDto> getMemberById(@PathVariable Long id) {
        return new SuccessResponse<>(memberService.getMemberById(id));
    }
}
