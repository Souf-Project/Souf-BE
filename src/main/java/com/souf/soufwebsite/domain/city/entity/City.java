package com.souf.soufwebsite.domain.city.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {
    @Id @GeneratedValue
    @Column(name = "city_id")
    private Long id;

    private String name;
}


