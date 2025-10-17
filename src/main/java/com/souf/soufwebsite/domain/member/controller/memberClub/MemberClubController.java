package com.souf.soufwebsite.domain.member.controller.memberClub;

import com.souf.soufwebsite.domain.member.dto.ResDto.ClubMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MyClubResDto;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import com.souf.soufwebsite.domain.member.service.club.MemberClubService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.member.controller.memberClub.MemberClubSuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs")
public class MemberClubController implements MemberClubApiSpecification {

    private final MemberClubService memberClubService;

    @Override
    @PostMapping("/{clubId}/join")
    public SuccessResponse<?> joinClub(
            @CurrentEmail String email,
            @PathVariable @NotNull Long clubId
    ) {
        memberClubService.joinClub(email, clubId);
        return new SuccessResponse<>(JOIN_CLUB_SUCCESS.getMessage());
    }

    @Override
    @DeleteMapping("/{clubId}/join")
    public SuccessResponse<?> leaveClub(
            @CurrentEmail String email,
            @PathVariable @NotNull Long clubId
    ) {
        memberClubService.leaveClub(email, clubId);
        return new SuccessResponse<>(LEAVE_CLUB_SUCCESS.getMessage());
    }

    @Override
    @GetMapping("/my")
    public SuccessResponse<Page<MyClubResDto>> getMyClubs(
            @CurrentEmail String email,
            @PageableDefault Pageable pageable
    ) {
        Page<MyClubResDto> clubs = memberClubService.getMyClubs(email, pageable);
        return new SuccessResponse<>(clubs, MY_CLUBS_READ_SUCCESS.getMessage());
    }

    @Override
    @GetMapping("/{clubId}/members")
    public SuccessResponse<Page<ClubMemberResDto>> getClubMembers(
            @PathVariable @NotNull Long clubId,
            @PageableDefault Pageable pageable
    ) {
        Page<ClubMemberResDto> clubMembers = memberClubService.getClubMembers(clubId, pageable);
        return new SuccessResponse<>(clubMembers, CLUB_MEMBERS_READ_SUCCESS.getMessage());
    }


    // 승인
    @PatchMapping("/{clubId}/members/{studentId}/approve")
    public SuccessResponse<?> approve(@CurrentEmail String clubEmail,
                                      @PathVariable Long clubId,
                                      @PathVariable Long studentId) {
        memberClubService.approveJoin(clubEmail, clubId, studentId);
        return new SuccessResponse<>("가입 신청을 승인했습니다.");
    }

    // 거절
    @PatchMapping("/{clubId}/members/{studentId}/reject")
    public SuccessResponse<?> reject(@CurrentEmail String clubEmail,
                                     @PathVariable Long clubId,
                                     @PathVariable Long studentId) {
        memberClubService.rejectJoin(clubEmail, clubId, studentId);
        return new SuccessResponse<>("가입 신청을 거절했습니다.");
    }

    // 대기 목록 조회
    @GetMapping("/{clubId}/pending")
    public SuccessResponse<Page<MemberClubMapping>> pending(@PathVariable Long clubId,
                                                            @PageableDefault Pageable pageable) {
        Page<MemberClubMapping> pendingMembers = memberClubService.getPendingMembers(clubId, pageable);
        return new SuccessResponse<>(pendingMembers, "대기 신청 목록");
    }
}
