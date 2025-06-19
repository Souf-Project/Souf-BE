package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitCategoryMapping;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record RecruitResDto(
        Long recruitId,
        String title,
        String content,
        String cityName,
        String regionName,
        LocalDateTime deadline,
        String minPayment,
        String maxPayment,
        String preferentialTreatment,
        String nickname,
        List<CategoryDto> categoryDtoList
) {
    public static RecruitResDto from(Recruit recruit, String nickname) {
        return new RecruitResDto(recruit.getId(),
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getCity().getName(),
                recruit.getRegion() != null ? recruit.getRegion().getName() : null,
                recruit.getDeadline(),
                recruit.getMinPayment(),
                recruit.getMaxPayment(),
                recruit.getPreferentialTreatment(),
                nickname,
                convertToCategoryDto(recruit.getCategories())
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<RecruitCategoryMapping> mappings){
        return mappings.stream().map(
                m -> new CategoryDto(m.getFirstCategory().getId(), m.getSecondCategory().getId(), m.getThirdCategory().getId())
        ).collect(Collectors.toList());
    }
}
