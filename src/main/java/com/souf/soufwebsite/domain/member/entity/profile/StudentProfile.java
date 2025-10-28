package com.souf.soufwebsite.domain.member.entity.profile;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.StudentSignupReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "student_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EducationType educationType;

    @Column
    private String schoolEmail;

    @OneToMany
    private List<Specialty> specialties;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public StudentProfile(StudentSignupReqDto reqDto, List<Specialty> specialties) {
        this.schoolName = reqDto.schoolName();
        this.educationType = reqDto.educationType();
        this.specialties = specialties;
        this.schoolEmail = reqDto.schoolEmail();
    }

    public void attachMember(Member member) {
        this.member = member;
    }
}