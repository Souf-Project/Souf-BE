package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "동아리 회원 목록 응답 DTO")
public record ClubMemberResDto(

        @Schema(description = "학생 ID", example = "12")
        Long studentId,

        @Schema(description = "학생 닉네임", example = "서정조")
        String nickname,

        @Schema(description = "학생 자기소개", example = "Spring Boot 백엔드 개발자 지망생입니다.")
        String intro,

        @Schema(description = "학생 개인 URL (포트폴리오, SNS 등)", example = "https://example.com/me")
        String personalUrl,

        @Schema(description = "가입 일자", example = "2025-10-10T15:30:00")
        LocalDateTime joinedAt
) {

    /**
     * Entity → DTO 변환
     */
    public static ClubMemberResDto from(MemberClubMapping membership) {
        Member student = membership.getStudent();

        return ClubMemberResDto.builder()
                .studentId(student.getId())
                .nickname(student.getNickname())
                .intro(student.getIntro())
                .personalUrl(student.getPersonalUrl())
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}
