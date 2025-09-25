package com.souf.soufwebsite.domain.review.dto;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitCategoryMapping;
import com.souf.soufwebsite.domain.review.entity.Review;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewResDto(
        String title,
        String content,
        Double score,
        Long viewCount,
        List<MediaResDto> mediaResDtos,

        Long recruitId,
        String recruitTitle,
        String price,
        String workType,
        String cityName,
        String cityDetailName,
        String startDate,
        String deadline,
        List<CategoryDto> categoryDtoList,

        Long recruiterId,
        String recruiterProfile,
        String recruiterNickname
) {

    public static ReviewResDto from(Review review, Recruit recruit, Member member, String profileImageUrl, List<Media> media) {
        return new ReviewResDto(
                review.getTitle(),
                review.getContent(),
                review.getScore(),
                review.getViewTotalCount(),
                convertToMediaResDto(media),

                recruit.getId(),
                recruit.getTitle(),
                recruit.getPrice(),
                recruit.getWorkType().name(),
                recruit.getCity().getName(),
                recruit.getCityDetail().getName(),
                convertToDateTime(recruit.getStartDate()),
                convertToDateTime(recruit.getDeadline()),
                convertToCategoryDto(recruit.getCategories()),

                member.getId(),
                profileImageUrl,
                member.getNickname()

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
                MediaResDto::fromMedia
        ).collect(Collectors.toList());
    }

    private static String convertToDateTime(LocalDateTime dateTime){

        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
