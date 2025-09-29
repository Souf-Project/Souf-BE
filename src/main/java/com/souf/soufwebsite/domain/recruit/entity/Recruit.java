package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.city.entity.City;
import com.souf.soufwebsite.domain.city.entity.CityDetail;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.exception.NotBlankPriceException;
import com.souf.soufwebsite.domain.recruit.exception.NotValidPricePolicyException;
import com.souf.soufwebsite.domain.socialAccount.exception.NotValidProviderException;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 3000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityDetail_id")
    private CityDetail cityDetail;

    @Column
    private LocalDateTime startDate;

    // 마감일자
    @Column
    private LocalDateTime deadline;

    // 가격 (FIXED일 때만 값이 있음, OFFER면 null)
    @Column
    private String price;

    // 가격 정책
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricePolicy pricePolicy;

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

    @Column
    private boolean isTaskCompleted; // 작업 완료 여부

    @Builder.Default
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RecruitCategoryMapping> categories = new ArrayList<>();

    // 작성자 (userId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Recruit of(RecruitReqDto reqDto, Member member, City city, CityDetail cityDetail) {
        PricePolicy pricePolicy = resolvePolicy(reqDto.price());
        return Recruit.builder()
                .title(reqDto.title())
                .content(reqDto.content())
                .city(city)
                .cityDetail(cityDetail)
                .startDate(reqDto.startDate())
                .deadline(reqDto.deadline())
                .price(reqDto.price())
                .pricePolicy(pricePolicy)
                .preferentialTreatment(reqDto.preferentialTreatment())
                .recruitCount(0L)
                .viewCount(0L)
                .recruitable(true)
                .workType(reqDto.workType())
                .isTaskCompleted(false)
                .member(member)
                .build();
    }
    public void updateRecruit(RecruitReqDto reqDto, City city, CityDetail cityDetail) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.city = city;
        this.cityDetail = cityDetail;
        this.startDate = reqDto.startDate();
        this.deadline = reqDto.deadline();
        this.price = reqDto.price();
        this.workType = reqDto.workType();
        this.preferentialTreatment = reqDto.preferentialTreatment();
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

    public void checkAndUpdateRecruitable() {
        if (this.deadline != null && this.deadline.isBefore(LocalDateTime.now())) {
            this.recruitable = false;
        }
    }

    public void updateRecruitable() {
        this.recruitable = false;
    }

    // ====== 정책 검증 ======
    @PrePersist
    @PreUpdate
    private void validatePricePolicy() {
        if (this.pricePolicy == PricePolicy.FIXED) {
            if (isBlank(this.price)) {
                throw new NotBlankPriceException();
            }
        } else if (this.pricePolicy == PricePolicy.OFFER) {
            this.price = null;
        } else {
            throw new NotValidPricePolicyException();
        }
    }

    private static PricePolicy resolvePolicy(String price) {
        return isBlank(price) ? PricePolicy.OFFER : PricePolicy.FIXED;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}