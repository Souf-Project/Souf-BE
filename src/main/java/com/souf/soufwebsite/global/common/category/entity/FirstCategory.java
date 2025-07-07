package com.souf.soufwebsite.global.common.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FirstCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "first_category_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "firstCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecondCategory> secondCategoryList = new ArrayList<>();

}