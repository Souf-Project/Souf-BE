package com.souf.soufwebsite.domain.city.repository;

import com.souf.soufwebsite.domain.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
