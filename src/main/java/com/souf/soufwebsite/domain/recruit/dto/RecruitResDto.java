package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
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
        String cityDetailName,
        LocalDateTime deadline,
        String minPayment,
        String maxPayment,
        String preferentialTreatment,
        String nickname,
        List<CategoryDto> categoryDtoList,
        List<MediaResDto> mediaResDtos
) {
    public static RecruitResDto from(Recruit recruit, String nickname, List<Media> mediaList) {
        return new RecruitResDto(recruit.getId(),
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getCity().getName(),
                recruit.getCityDetail() != null ? recruit.getCityDetail().getName() : null,
                recruit.getDeadline(),
                recruit.getMinPayment(),
                recruit.getMaxPayment(),
                recruit.getPreferentialTreatment(),
                nickname,
                convertToCategoryDto(recruit.getCategories()),
                convertToMediaResDto(mediaList)
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<RecruitCategoryMapping> mappings){
        return mappings.stream().map(
                m -> new CategoryDto(m.getFirstCategory().getId(), m.getSecondCategory().getId(), m.getThirdCategory().getId())
        ).collect(Collectors.toList());
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(Collectors.toList());
    }
}
