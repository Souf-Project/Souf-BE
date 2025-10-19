package com.souf.soufwebsite.domain.member.controller.memberClub;

import com.souf.soufwebsite.domain.member.dto.ResDto.ClubSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MyClubResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "동아리", description = "회원 동아리 관리 API")
public interface MemberClubApiSpecification {

    @Operation(summary = "동아리 전체 조회", description = "모든 동아리의 간단한 정보들을 페이징 처리하여 조회합니다.")
    @GetMapping
    SuccessResponse<Page<ClubSimpleResDto>> getAllClubs(
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "동아리 가입", description = "회원이 특정 동아리에 가입 요청을 합니다.")
    @PostMapping("/{clubId}/join")
    SuccessResponse<?> joinClub(
            @CurrentEmail String email,
            @PathVariable @NotNull Long clubId
    );

    @Operation(summary = "동아리 탈퇴", description = "회원이 특정 동아리에서 탈퇴합니다.")
    @DeleteMapping("/{clubId}/withdraw")
    SuccessResponse<?> leaveClub(
            @CurrentEmail String email,
            @PathVariable @NotNull Long clubId
    );

    @Operation(summary = "동아리 회원 목록 조회", description = "특정 동아리에 가입한 회원들의 정보를 페이징 처리하여 조회합니다.")
    @GetMapping("/{clubId}/members")
    SuccessResponse<Page<MemberSimpleResDto>> getClubMembers(
            @PathVariable @NotNull Long clubId,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "내 동아리 목록 조회", description = "회원이 가입한 동아리들의 정보를 페이징 처리하여 조회합니다.")
    @GetMapping("/my")
    SuccessResponse<Page<MyClubResDto>> getMyClubs(
            @CurrentEmail String email,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "동아리 가입 승인", description = "동아리 관리자가 특정 회원의 가입 요청을 승인합니다.")
    @PatchMapping("/{clubId}/members/{studentId}/approve")
    SuccessResponse<?> approve(@CurrentEmail String clubEmail,
            @PathVariable Long clubId,
            @PathVariable Long studentId
    );

    @Operation(summary = "동아리 가입 거절", description = "동아리 관리자가 특정 회원의 가입 요청을 거절합니다.")
    @PatchMapping("/{clubId}/members/{studentId}/reject")
    SuccessResponse<?> reject(@CurrentEmail String clubEmail,
            @PathVariable Long clubId,
            @PathVariable Long studentId
    );

    @Operation(summary = "동아리 가입 대기 회원 목록 조회", description = "특정 동아리에 가입 요청을 한 회원들의 정보를 페이징 처리하여 조회합니다.")
    @GetMapping("/{clubId}/pending")
    SuccessResponse<Page<MemberSimpleResDto>> pending(
            @PathVariable Long clubId,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "동아리 회원 추방", description = "동아리 관리자가 특정 회원을 동아리에서 추방합니다.")
    @PatchMapping("/{clubId}/members/{studentId}/expel")
    SuccessResponse<?> expelMember(@CurrentEmail String clubEmail,
            @PathVariable Long clubId,
            @PathVariable Long studentId
    );
}
