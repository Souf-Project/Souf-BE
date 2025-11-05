package com.souf.soufwebsite.domain.member.entity.profile;

import com.souf.soufwebsite.domain.member.dto.reqDto.addInfo.AddStudentInfoReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.StudentSignupReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "student_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentProfile extends BaseEntity {

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

    @OneToMany(mappedBy = "studentProfile", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Specialty> specialties = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public StudentProfile(StudentSignupReqDto reqDto) {
        this.schoolName = reqDto.getSchoolName();
        this.educationType = reqDto.getEducationType();
        this.schoolEmail = reqDto.getSchoolEmail();
    }

    public StudentProfile(AddStudentInfoReqDto reqDto) {
        this.schoolName = reqDto.schoolName();
        this.educationType = reqDto.educationType();
        this.schoolEmail = reqDto.schoolEmail();
    }

    public void attachMember(Member member) {
        this.member = member;
    }

    public void addSpecialty(Specialty specialty) {
        specialties.add(specialty);
        specialty.attachStudentProfile(this);
    }

    public void updateFrom(AddStudentInfoReqDto req){
        this.schoolName = req.schoolName();
        this.educationType = req.educationType();
        this.schoolEmail = req.schoolEmail();

    }
}