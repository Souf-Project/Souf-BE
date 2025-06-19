package com.souf.soufwebsite.domain.city.dto;

import com.souf.soufwebsite.domain.city.entity.City;
import com.souf.soufwebsite.domain.region.dto.RegionDto;

import java.util.List;

public record CityDto(
        Long id,
        String name
) {
    public static CityDto from(City city) {
        List<RegionDto> regionDtos = city.getRegions().stream()
                .map(RegionDto::from)
                .toList();

        return new CityDto(city.getId(), city.getName());
    }
}