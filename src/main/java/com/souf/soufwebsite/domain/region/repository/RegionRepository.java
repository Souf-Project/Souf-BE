package com.souf.soufwebsite.domain.region.repository;

import com.souf.soufwebsite.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByCityId(Long cityId);
}
