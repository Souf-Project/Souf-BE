package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.exception.NotValidDeadLineException;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import com.souf.soufwebsite.global.common.FirstCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    @Size(max = 50)
    private String region;

    // 마감일자
    @Column
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String payment;

    @Column
    private String preferentialTreatment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FirstCategory firstCategory;

    // 작성자 (userId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @Builder
    public Recruit(String title, String content, String region, LocalDateTime deadline, String payment,
                   String preferentialTreatment, FirstCategory firstCategory, Member member) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.deadline = deadline;
        this.payment = payment;
        this.preferentialTreatment = preferentialTreatment;
        this.firstCategory = firstCategory;
        this.member = member;
    }

    public static Recruit of(RecruitReqDto reqDto, Member member) {
        return Recruit.builder()
                .title(reqDto.title())
                .content(reqDto.content())
                .region(reqDto.region())
                .deadline(reqDto.deadline())
                .payment(reqDto.payment())
                .preferentialTreatment(reqDto.preferentialTreatment())
                .firstCategory(reqDto.firstCategory())
                .member(member)
                .build();
    }
    public void updateRecruit(RecruitReqDto reqDto) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.region = reqDto.region();
        this.deadline = reqDto.deadline();
        this.payment = reqDto.payment();
        this.preferentialTreatment = reqDto.preferentialTreatment();
        this.firstCategory = reqDto.firstCategory();
    }

    public void addFileOnRecruit(File file){
        this.files.add(file);
        file.assignToRecruit(this);
    }
}