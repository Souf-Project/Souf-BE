package com.souf.soufwebsite.domain.city.controller;

import com.souf.soufwebsite.domain.city.dto.CityDto;
import com.souf.soufwebsite.domain.city.repository.CityRepository;
import com.souf.soufwebsite.domain.region.dto.RegionDto;
import com.souf.soufwebsite.domain.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;

    @GetMapping
    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream()
                .map(city -> new CityDto(city.getId(), city.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{cityId}/regions")
    public List<RegionDto> getRegionsByCityId(@PathVariable Long cityId) {
        return regionRepository.findByCityId(cityId).stream()
                .map(region -> new RegionDto(region.getId(), region.getName(), cityId))
                .collect(Collectors.toList());
    }
}
