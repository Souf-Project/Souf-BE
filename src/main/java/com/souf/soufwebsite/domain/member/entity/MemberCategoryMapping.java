package com.souf.soufwebsite.domain.member.entity;

import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private FirstCategory firstCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private SecondCategory secondCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private ThirdCategory thirdCategory;

    public static MemberCategoryMapping of(Member member, FirstCategory first, SecondCategory second, ThirdCategory third) {
        MemberCategoryMapping mapping = new MemberCategoryMapping();
        mapping.firstCategory = first;
        mapping.secondCategory = second;
        mapping.thirdCategory = third;
        return mapping;
    }

    public boolean isSameCategorySet(MemberCategoryMapping mapping) {
        return this.firstCategory.getId().equals(mapping.firstCategory.getId())
                && this.secondCategory.getId().equals(mapping.secondCategory.getId())
                && this.thirdCategory.getId().equals(mapping.thirdCategory.getId());
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void disconnectMember() {
        this.member = null;
    }
}