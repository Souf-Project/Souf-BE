package com.souf.soufwebsite.domain.member.entity.profile;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "specialties")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specialtyName;

    @Column(nullable = false)
    private MajorType specialtyType;

    public Specialty(String specialtyName, MajorType specialtyType) {
        this.specialtyName = specialtyName;
        this.specialtyType = specialtyType;
    }
}
