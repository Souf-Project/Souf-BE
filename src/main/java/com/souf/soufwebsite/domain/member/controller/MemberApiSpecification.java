package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
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

    @Operation(summary = "회원 목록 조회", description = "모든 회원의 정보를 페이징하여 조회합니다.")
    @GetMapping("/member")
    SuccessResponse<Page<MemberResDto>> getMembers(
            @PageableDefault(size = 6)
            Pageable pageable
    );

    @Operation(summary = "내 정보 조회", description = "로그인된 사용자의 회원 정보를 조회합니다.")
    @GetMapping("/member/myinfo")
    SuccessResponse<MemberResDto> getMyInfo();

    @Operation(summary = "회원 단건 조회", description = "회원 ID로 특정 회원의 상세 정보를 조회합니다.")
    @GetMapping("/member/{id}")
    SuccessResponse<MemberResDto> getMemberById(
            @PathVariable Long id
    );

    @Operation(summary = "카테고리로 회원 검색", description = "1~3차 카테고리 필터에 해당하는 회원 목록을 페이징하여 조회합니다.")
    @GetMapping("/member/search")
    SuccessResponse<Page<MemberResDto>> findByCategory(
            @RequestParam(required = false) Long first,
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

    @Operation(summary = "회원정보 수정", description = "로그인된 사용자의 회원정보를 업데이트합니다.")
    @PutMapping("/update")
    SuccessResponse<?> updateUserInfo(
            @RequestBody @Valid UpdateReqDto reqDto
    );
}