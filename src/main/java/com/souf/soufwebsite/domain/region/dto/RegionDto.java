package com.souf.soufwebsite.domain.region.dto;

import com.souf.soufwebsite.domain.region.entity.Region;

public record RegionDto(
        Long id,
        String name,
        Long cityId
) {
    public static RegionDto from(Region region) {
        return new RegionDto(region.getId(), region.getName(), region.getCity().getId());
    }
}