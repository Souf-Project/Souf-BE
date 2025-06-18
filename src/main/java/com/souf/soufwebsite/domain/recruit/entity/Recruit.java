package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    @Size(max = 50)
    @Enumerated(EnumType.STRING)
    private RegionType region;

    // 마감일자
    @Column
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String payment;

    @Column
    private String preferentialTreatment;

    @Column(nullable = false)
    private Long recruitCount;

    @Column
    private Long viewCount;

    @NotNull
    @Column(nullable = false)
    private boolean recruitable;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Builder.Default
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RecruitCategoryMapping> categories = new ArrayList<>();

    // 작성자 (userId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> media = new ArrayList<>();

    public static Recruit of(RecruitReqDto reqDto, Member member) {
        return Recruit.builder()
                .title(reqDto.title())
                .content(reqDto.content())
                .region(reqDto.region())
                .deadline(reqDto.deadline())
                .payment(reqDto.payment())
                .preferentialTreatment(reqDto.preferentialTreatment())
                .recruitCount(0L)
                .viewCount(0L)
                .recruitable(true)
                .workType(reqDto.workType())
                .member(member)
                .build();
    }
    public void updateRecruit(RecruitReqDto reqDto) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.region = reqDto.region();
        this.deadline = reqDto.deadline();
        this.payment = reqDto.payment();
        this.workType = reqDto.workType();
        this.preferentialTreatment = reqDto.preferentialTreatment();
    }

    public void addMediaOnRecruit(Media media){
        this.media.add(media);
        media.assignToRecruit(this);
    }

    public void addCategory(RecruitCategoryMapping recruitCategoryMapping){
        this.categories.add(recruitCategoryMapping);
    }

    public void clearCategories() {
        for (RecruitCategoryMapping mapping : categories) {
            mapping.disconnectRecruit();
        }
        categories.clear();
    }

    public void increaseRecruitCount() {
        this.recruitCount++;
    }

    public void decreaseRecruitCount() {
        this.recruitCount--;
    }
}