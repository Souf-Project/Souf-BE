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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MajorType specialtyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    public Specialty(String specialtyName, MajorType specialtyType) {
        this.specialtyName = specialtyName;
        this.specialtyType = specialtyType;
    }

    void attachStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }
}
