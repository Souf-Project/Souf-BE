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
        String deadLine,
        Long recruitCount,
        String nickname,
        boolean recruitable,
        LocalDateTime lastModified
) {
    public static RecruitSimpleResDto of(Long recruitId, String title, Long secondCategoryId, String content,
                                         String price, String cityName, String cityDetailName,
                                         LocalDateTime deadLine, Long recruitCount, String nickname,
                                         boolean recruitable, LocalDateTime lastModified) {
        return new RecruitSimpleResDto(
                recruitId, title,
                new ArrayList<>(List.of(secondCategoryId)), // 초기 리스트
                content, price, cityName, cityDetailName,
                convertToDateTime(deadLine), recruitCount, nickname,
                recruitable, lastModified
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
