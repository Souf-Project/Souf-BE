package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관련 API")
@RequestMapping("/api/v1")
public interface MemberApiSpecification {

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/auth/signup")
    SuccessResponse<?> signup(
            @RequestBody @Valid SignupReqDto reqDto
    );

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/auth/login")
    SuccessResponse<TokenDto> signin(
            @RequestBody @Valid SigninReqDto reqDto
    );

    @Operation(summary = "비밀번호 재설정", description = "입력한 새 비밀번호로 계정 비밀번호를 변경합니다.")
    @PatchMapping("/auth/reset/password")
    SuccessResponse<?> resetPassword(
            @RequestBody @Valid ResetReqDto reqDto
    );

    @Operation(summary = "이메일 인증번호 전송", description = "입력된 이메일로 인증번호를 전송합니다.")
    @PostMapping("/auth/email/send")
    SuccessResponse<Boolean> sendEmailVerification(
            @RequestParam String email
    );

    @Operation(summary = "이메일 인증번호 검증", description = "이메일로 발급된 인증번호와 일치하게 입력하였는지 검증합니다.")
    @PostMapping("/auth/email/verify")
    SuccessResponse<Boolean> verifyEmailCode(
            @RequestParam String email,
            @RequestParam String code
    );

    @Operation(summary = "회원정보 수정", description = "로그인된 사용자의 회원정보를 업데이트합니다.")
    @PutMapping("/auth/update")
    SuccessResponse<?> updateUserInfo(
            @RequestBody @Valid UpdateReqDto reqDto
    );

    @Operation(summary = "회원 목록 조회", description = "모든 회원의 정보를 페이징하여 조회합니다.")
    @GetMapping("/member")
    SuccessResponse<Page<MemberResDto>> getMembers(
            @PageableDefault(size = 6)
            Pageable pageable
    );

    @Operation(summary = "회원 단건 조회", description = "회원 ID로 특정 회원의 상세 정보를 조회합니다.")
    @GetMapping("/member/{id}")
    SuccessResponse<MemberResDto> getMemberById(
            @PathVariable Long id
    );

    @Operation(summary = "카테고리로 회원 검색", description = "1~3차 카테고리 필터에 해당하는 회원 목록을 페이징하여 조회합니다.")
    @GetMapping("/member/search")
    SuccessResponse<Page<MemberResDto>> findByCategory(
            @RequestParam(required = false) Long first,
            @RequestParam(required = false) Long second,
            @RequestParam(required = false) Long third,
            @PageableDefault(size = 6)
            Pageable pageable
    );

    @Operation(summary = "닉네임으로 회원 검색", description = "닉네임에 키워드(검색어)가 포함된 회원 목록을 페이징하여 조회합니다.")
    @GetMapping("/member/search/nickname")
    SuccessResponse<Page<MemberResDto>> findByNickname(
            @RequestParam String keyword,
            @PageableDefault(size = 6)
            Pageable pageable
    );

}