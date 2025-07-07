package com.souf.soufwebsite.global.common.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecondCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "second_category_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_category_id", nullable = false)
    private FirstCategory firstCategory;

    @OneToMany(mappedBy = "secondCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThirdCategory> thirdCategoryList = new ArrayList<>();
}