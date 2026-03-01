package com.souf.soufwebsite.domain.member.dto.resDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.util.List;
import java.util.stream.Collectors;

public record MemberSummaryDto(
        Long id,
        String nickname,
        String intro,
        double temperature,
        List<CategoryDto> memberCategories
) {

    public static MemberSummaryDto of(Member member) {
        return new MemberSummaryDto(member.getId(), member.getNickname(), member.getIntro(), member.getTemperature(), convertToCategoryDto(member.getCategories()));
    }

    private static List<CategoryDto> convertToCategoryDto(List<MemberCategoryMapping> mappings){
        return mappings.stream().map(
                m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory() != null ? m.getThirdCategory().getId() : null
                )).collect(Collectors.toList());
    }
}
