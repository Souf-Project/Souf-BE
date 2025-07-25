package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitCategoryMapping;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public record RecruitResDto(

        Long memberId,
        Long recruitId,
        String title,
        String content,
        String cityName,
        String cityDetailName,
        String deadline,
        String minPayment,
        String maxPayment,
        String preferentialTreatment,
        String nickname,
        boolean recruitable,
        List<CategoryDto> categoryDtoList,
        List<MediaResDto> mediaResDtos
) {
    public static RecruitResDto from(Long memberId, Recruit recruit, String nickname, List<Media> mediaList) {
        return new RecruitResDto(
                memberId,
                recruit.getId(),
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getCity().getName(),
                recruit.getCityDetail() != null ? recruit.getCityDetail().getName() : null,
                convertToDateTime(recruit.getDeadline()),
                recruit.getMinPayment(),
                recruit.getMaxPayment(),
                recruit.getPreferentialTreatment(),
                nickname,
                recruit.isRecruitable(),
                convertToCategoryDto(recruit.getCategories()),
                convertToMediaResDto(mediaList)
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<RecruitCategoryMapping> mappings){
        return mappings.stream().map(
                m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory() != null ? m.getThirdCategory().getId() : null
        )).collect(Collectors.toList());
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(Collectors.toList());
    }

    private static String convertToDateTime(LocalDateTime dateTime){

        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
