package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Schema(description = "동아리 회원 목록 응답 DTO (Member 리스트용 필드 + joinedAt 포함)")
public record ClubMemberResDto(
        Long memberId,
        Double temperature,
        String profileImageUrl,
        String nickname,
        String intro,
        List<CategoryDto> categoryDtoList,
        List<PopularFeedDto> popularFeeds
) {
    public record PopularFeedDto(
            String imageUrl
    ) {}

    public static ClubMemberResDto from(
            MemberClubMapping memberClubMapping,
            String profileImageUrl,
            List<PopularFeedDto> popularFeeds,
            List<MemberCategoryMapping> categories
    ) {
        Member student = memberClubMapping.getStudent();

        return ClubMemberResDto.builder()
                .memberId(student.getId())
                .temperature(student.getTemperature())
                .profileImageUrl(profileImageUrl)
                .nickname(student.getNickname())
                .intro(student.getIntro())
                .categoryDtoList(convertToCategoryDto(categories))
                .popularFeeds(popularFeeds != null ? popularFeeds : List.of())
                .build();
    }

    private static List<CategoryDto> convertToCategoryDto(List<MemberCategoryMapping> mappings) {
        if (mappings == null) return new ArrayList<>();
        return mappings.stream()
                .map(m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory()  != null ? m.getThirdCategory().getId()  : null
                ))
                .collect(Collectors.toList());
    }
}