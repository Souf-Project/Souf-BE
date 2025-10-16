package com.souf.soufwebsite.domain.member.controller.memberClub;

import com.souf.soufwebsite.domain.member.dto.ResDto.ClubMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MyClubResDto;
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
            @PathVariable @NotNull Long clubId,
            @RequestParam(required = false, defaultValue = "MEMBER") String roleInClub
    ) {
        memberClubService.joinClub(email, clubId, roleInClub);
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
        Page<MyClubResDto> page = memberClubService.getMyClubs(email, pageable);
        return new SuccessResponse<>(page, MY_CLUBS_READ_SUCCESS.getMessage());
    }

    @Override
    @GetMapping("/{clubId}/members")
    public SuccessResponse<Page<ClubMemberResDto>> getClubMembers(
            @PathVariable @NotNull Long clubId,
            @PageableDefault Pageable pageable
    ) {
        Page<ClubMemberResDto> page = memberClubService.getClubMembers(clubId, pageable);
        return new SuccessResponse<>(page, CLUB_MEMBERS_READ_SUCCESS.getMessage());
    }
}