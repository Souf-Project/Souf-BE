package com.souf.soufwebsite.domain.member.dto.ResDto;


import com.souf.soufwebsite.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "동아리 목록 요약 DTO")
public record ClubSimpleResDto(
        @Schema(description = "동아리 ID")
        Long clubId,
        @Schema(description = "동아리명(닉네임 사용)")
        String clubName,
        @Schema(description = "소개")
        String intro,
        @Schema(description = "프로필/대표 URL")
        String profileImageUrl,
        @Schema(description = "동아리원 수")
        Long memberCount
) {
    public static ClubSimpleResDto from(Member club, Long memberCount) {
        return ClubSimpleResDto.builder()
                .clubId(club.getId())
                .clubName(club.getNickname())
                .intro(club.getIntro())
                .profileImageUrl(club.getPersonalUrl())
                .memberCount(memberCount)
                .build();
    }
}