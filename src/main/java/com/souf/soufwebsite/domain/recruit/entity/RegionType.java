package com.souf.soufwebsite.domain.recruit.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum RegionType {
    GANGNAM_GU("강남구"),
    GANGDONG_GU("강동구"),
    GANGBUK_GU("강북구"),
    GANGSEO_GU("강서구"),
    GWANAK_GU("관악구"),
    GWANGJIN_GU("광진구"),
    GURO_GU("구로구"),
    GEUMCHEON_GU("금천구"),
    NOWON_GU("노원구"),
    DOBONG_GU("도봉구"),
    DONGDAEMUN_GU("동대문구"),
    DONGJAK_GU("동작구"),
    MAPO_GU("마포구"),
    SEODAEMUN_GU("서대문구"),
    SEOCHO_GU("서초구"),
    SEONGDONG_GU("성동구"),
    SEONGBUK_GU("성북구"),
    SONGPA_GU("송파구"),
    YANGCHEON_GU("양천구"),
    YEONGDEUNGPO_GU("영등포구"),
    YONGSAN_GU("용산구"),
    EUNPYEONG_GU("은평구"),
    JONGNO_GU("종로구"),
    JUNG_GU("중구"),
    JUNGRANG_GU("중랑구"),
    NON_REGION("지역무관");

    private final String region;

    @JsonValue
    public String getRegion() {
        return region;
    }

    @JsonCreator
    public static RegionType from(String region) {
        return Stream.of(RegionType.values())
                .filter(r -> r.region.equals(region))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 지역명입니다: " + region));
    }
}
