package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record MemberSimpleResDto(
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

    public static MemberSimpleResDto from(Member member, String profileImageUrl, List<PopularFeedDto> popularFeeds, List<MemberCategoryMapping> categories) {
        return new MemberSimpleResDto(
                member.getId(),
                member.getTemperature(),
                profileImageUrl,
                member.getNickname(),
                member.getIntro(),
                convertToCategoryDto(categories),
                popularFeeds
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<MemberCategoryMapping> mappings){
        if(mappings == null) return new ArrayList<>();
        return mappings.stream().map(
                m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory() != null ? m.getThirdCategory().getId() : null
                )).collect(Collectors.toList());
    }
}
