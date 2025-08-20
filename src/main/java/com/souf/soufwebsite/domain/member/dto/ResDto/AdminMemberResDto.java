package com.souf.soufwebsite.domain.member.dto.ResDto;

public record AdminMemberResDto(

        Long memberId,
        String roleType,
        String username,
        String nickname,
        String email,
        int cumulativeReports,
        Boolean isDeleted
) {
}
