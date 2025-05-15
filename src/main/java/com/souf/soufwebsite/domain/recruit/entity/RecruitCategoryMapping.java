package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Recruit recruit;

    @ManyToOne(fetch = FetchType.LAZY)
    private FirstCategory firstCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private SecondCategory secondCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private ThirdCategory thirdCategory;

    public static RecruitCategoryMapping of(Recruit recruit, FirstCategory firstCategory, SecondCategory secondCategory, ThirdCategory thirdCategory) {
        RecruitCategoryMapping mapping = new RecruitCategoryMapping();
        mapping.recruit = recruit;
        mapping.firstCategory = firstCategory;
        mapping.secondCategory = secondCategory;
        mapping.thirdCategory = thirdCategory;
        return mapping;
    }

    public void disconnectRecruit(){
        this.recruit = null;
    }

}
