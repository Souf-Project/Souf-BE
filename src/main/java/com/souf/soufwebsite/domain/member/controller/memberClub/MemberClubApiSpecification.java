package com.souf.soufwebsite.domain.member.controller.memberClub;

import com.souf.soufwebsite.domain.member.dto.ResDto.ClubMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MyClubResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "동아리", description = "회원 동아리 관리 API")
public interface MemberClubApiSpecification {

    @PostMapping("/{clubId}/join")
    SuccessResponse<?> joinClub(
            @CurrentEmail String email,
            @PathVariable @NotNull Long clubId
    );

    @DeleteMapping("/{clubId}/withdraw")
    SuccessResponse<?> leaveClub(
            @CurrentEmail String email,
            @PathVariable @NotNull Long clubId
    );

    @GetMapping("/{clubId}/members")
    SuccessResponse<Page<ClubMemberResDto>> getClubMembers(
            @PathVariable @NotNull Long clubId,
            @PageableDefault Pageable pageable
    );

    @GetMapping("/my")
    SuccessResponse<Page<MyClubResDto>> getMyClubs(
            @CurrentEmail String email,
            @PageableDefault Pageable pageable
    );

    @PatchMapping("/{clubId}/members/{studentId}/approve")
    SuccessResponse<?> approve(@CurrentEmail String clubEmail,
            @PathVariable Long clubId,
            @PathVariable Long studentId
    );

    @PatchMapping("/{clubId}/members/{studentId}/reject")
    SuccessResponse<?> reject(@CurrentEmail String clubEmail,
            @PathVariable Long clubId,
            @PathVariable Long studentId
    );

    @GetMapping("/{clubId}/pending")
    SuccessResponse<Page<ClubMemberResDto>> pending(
            @PathVariable Long clubId,
            @PageableDefault Pageable pageable
    );
}
