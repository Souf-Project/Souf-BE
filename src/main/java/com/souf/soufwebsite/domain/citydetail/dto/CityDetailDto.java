package com.souf.soufwebsite.domain.citydetail.dto;

import com.souf.soufwebsite.domain.citydetail.entity.CityDetail;

public record CityDetailDto(
        Long id,
        String name,
        Long cityId
) {
    public static CityDetailDto from(CityDetail cityDetail) {
        return new CityDetailDto(cityDetail.getId(), cityDetail.getName(), cityDetail.getCity().getId());
    }
}