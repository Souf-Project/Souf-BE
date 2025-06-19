package com.souf.soufwebsite.domain.city.entity;

import com.souf.soufwebsite.domain.region.entity.Region;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {
    @Id @GeneratedValue
    @Column(name = "city_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "city")
    private List<Region> regions = new ArrayList<>();
}


