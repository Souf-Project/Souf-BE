package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberUpdateResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "회원 정보 관련 API")
@RequestMapping("/api/v1/member")
public interface MemberApiSpecification {

    @Operation(summary = "회원 목록 조회", description = "카테고리와 검색어로 필터링한 회원 목록을 페이징하여 조회합니다.")
    @GetMapping("/member")
    SuccessResponse<Page<MemberSimpleResDto>> getMembers(
            @RequestParam(name = "firstCategory") Long first,
            @RequestParam(name = "secondCategory", required = false) Long second,
            @RequestParam(name = "thirdCategory", required = false) Long third,
            @RequestParam String keyword,
            @PageableDefault(size = 6) Pageable pageable
    );

    @Operation(summary = "내 정보 조회", description = "로그인된 사용자의 회원 정보를 조회합니다.")
    @GetMapping("/member/myinfo")
    SuccessResponse<MemberResDto> getMyInfo();

    @Operation(summary = "회원 단건 조회", description = "회원 ID로 특정 회원의 상세 정보를 조회합니다.")
    @GetMapping("/member/{id}")
    SuccessResponse<MemberResDto> getMemberById(
            @PathVariable Long id
    );

//    @Operation(summary = "카테고리로 회원 검색", description = "1~3차 카테고리 필터에 해당하는 회원 목록을 페이징하여 조회합니다.")
//    @GetMapping("/member/search")
//    SuccessResponse<Page<MemberResDto>> findByCategory(
//            @RequestParam(required = false) Long first,
//            @PageableDefault(size = 6)
//            Pageable pageable
//    );
//
//    @Operation(summary = "닉네임으로 회원 검색", description = "닉네임에 키워드(검색어)가 포함된 회원 목록을 페이징하여 조회합니다.")
//    @GetMapping("/member/search/nickname")
//    SuccessResponse<Page<MemberResDto>> findByNickname(
//            @RequestParam String keyword,
//            @PageableDefault(size = 6)
//            Pageable pageable
//    );

    @Operation(summary = "회원정보 수정", description = "로그인된 사용자의 회원정보를 업데이트합니다.")
    @PutMapping("/update")
    SuccessResponse<MemberUpdateResDto> updateUserInfo(
            @RequestBody @Valid UpdateReqDto reqDto
    );

    @Operation(summary = "이메일 인증번호 전송", description = "기존 회원의 이메일 인증을 위해 인증번호를 전송합니다.")
    @PostMapping("/modify/email/send")
    SuccessResponse<Boolean> sendModifyEmailVerification(
            @RequestParam String originalEmail,
            @RequestParam String acKrEmail
    );

    @Operation(summary = "회원프로필 업로드", description = "회원프로필을 업로드한 후의 파일정보를 저장합니다.")
    @PostMapping("/upload")
    SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto);
}