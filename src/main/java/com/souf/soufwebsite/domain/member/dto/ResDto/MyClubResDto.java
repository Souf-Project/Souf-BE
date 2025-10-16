package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "내 동아리 목록 응답 DTO")
public record MyClubResDto(

        @Schema(description = "동아리 ID", example = "5")
        Long clubId,

        @Schema(description = "동아리 이름", example = "TAVE 16기")
        String clubName,

        @Schema(description = "동아리 소개", example = "IT, 개발, 디자인이 함께하는 융합 동아리")
        String clubIntro,

        @Schema(description = "동아리 프로필 URL", example = "https://example.com/profile.png")
        String clubProfileUrl,

        @Schema(description = "가입 일자", example = "2025-10-15T12:30:00")
        LocalDateTime joinedAt
) {

    public static MyClubResDto from(MemberClubMapping membership) {
        Member club = membership.getClub();

        return MyClubResDto.builder()
                .clubId(club.getId())
                .clubName(club.getNickname())
                .clubIntro(club.getIntro())
                .clubProfileUrl(club.getPersonalUrl())
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}