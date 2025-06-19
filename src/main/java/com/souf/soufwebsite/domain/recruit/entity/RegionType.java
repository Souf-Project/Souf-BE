package com.souf.soufwebsite.domain.recruit.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum RegionType {

    // ✅ 서울
    GANGNAM_GU("서울", "강남구"),
    GANGDONG_GU("서울", "강동구"),
    GANGBUK_GU("서울", "강북구"),
    GANGSEO_GU("서울", "강서구"),
    GWANAK_GU("서울", "관악구"),
    GWANGJIN_GU("서울", "광진구"),
    GURO_GU("서울", "구로구"),
    GEUMCHEON_GU("서울", "금천구"),
    NOWON_GU("서울", "노원구"),
    DOBONG_GU("서울", "도봉구"),
    DONGDAEMUN_GU("서울", "동대문구"),
    DONGJAK_GU("서울", "동작구"),
    MAPO_GU("서울", "마포구"),
    SEODAEMUN_GU("서울", "서대문구"),
    SEOCHO_GU("서울", "서초구"),
    SEONGDONG_GU("서울", "성동구"),
    SEONGBUK_GU("서울", "성북구"),
    SONGPA_GU("서울", "송파구"),
    YANGCHEON_GU("서울", "양천구"),
    YEONGDEUNGPO_GU("서울", "영등포구"),
    YONGSAN_GU("서울", "용산구"),
    EUNPYEONG_GU("서울", "은평구"),
    JONGNO_GU("서울", "종로구"),
    JUNG_GU("서울", "중구"),
    JUNGRANG_GU("서울", "중랑구"),

    // ✅ 경기
    GOYANG_ILSAN("경기", "고양"),
    SUWON("경기", "수원역"),
    DONGSUWON("경기", "동수원"),
    SEONGNAM("경기", "성남"),
    BUCHEON("경기", "부천"),
    YONGIN("경기", "용인"),
    GIMPO("경기", "김포"),
    PAJU("경기", "파주"),
    ANSAN_ETC("경기", "안산"),
    ANYANG_ETC("경기", "안양"),
    HWASEONG_ETC("경기", "화성"),
    NAMYANGJU_ETC("경기", "남양주"),
    UIJEONGBU_ETC("경기", "의정부"),
    HANAM_ETC("경기", "하남"),
    YEOJU_ETC("경기", "여주"),
    POCHEON_YEONCHEON("경기", "포천"),

    // ✅ 무관
    NON_REGION("지역무관", "지역무관");

    private final String city;
    private final String region;

    @JsonValue
    public String getCity() { return city; }

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
