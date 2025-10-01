package com.souf.soufwebsite.domain.recruit.dto.res;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record RecruitSimpleResDto(

        Long recruitId,
        String title,
        List<Long> secondCategory,
        String content,
        String price,
        String cityName,
        String cityDetailName,
        String startDate,
        String deadLine,
        Long recruitCount,
        boolean recruitable,
        LocalDateTime lastModified,
        Long writerId,
        String nickname,
        String profileImageUrl,
        String firstMediaUrl
) {
    public static RecruitSimpleResDto of(Long recruitId, String title, Long secondCategoryId, String content,
                                         String price, String cityName, String cityDetailName,
                                         LocalDateTime startDate, LocalDateTime deadLine, Long recruitCount,
                                         boolean recruitable, LocalDateTime lastModified,
                                         Long writerId, String firstMediaUrl) {
        return new RecruitSimpleResDto(
                recruitId, title,
                new ArrayList<>(List.of(secondCategoryId)), // 초기 리스트
                content, price, cityName, cityDetailName,
                convertToDateTime(startDate), convertToDateTime(deadLine), recruitCount,
                recruitable, lastModified,
                writerId, null, null, firstMediaUrl
        );
    }

    public RecruitSimpleResDto withWriter(String nickname, String profileImageUrl) {
        return new RecruitSimpleResDto(
                recruitId, title, secondCategory, content, price,
                cityName, cityDetailName, deadLine, recruitCount,
                recruitable, lastModified,
                writerId, nickname, profileImageUrl, firstMediaUrl
        );
    }

    public void addSecondCategory(Long id) {
        if (!this.secondCategory.contains(id)) {
            this.secondCategory.add(id);
        }
    }

    private static String convertToDateTime(LocalDateTime dateTime){

        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
