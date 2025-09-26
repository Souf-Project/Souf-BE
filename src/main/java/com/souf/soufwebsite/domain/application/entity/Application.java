package com.souf.soufwebsite.domain.application.entity;

import com.souf.soufwebsite.domain.application.exception.OfferRequiredException;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.PricePolicy;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.exception.NotBlankPriceException;
import com.souf.soufwebsite.domain.recruit.exception.NotValidPricePolicyException;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Application extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @Column(nullable = false)
    private String priceOffer;

    @Column
    private String priceReason;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Builder
    private Application(Member member, Recruit recruit, String priceOffer, String priceReason) {
        this.member = member;
        this.recruit = recruit;
        this.priceOffer = priceOffer;
        this.priceReason = priceReason;
        this.status = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    /** FIXED 정책 지원용 팩토리 (가격/사유 입력 불필요) */
    public static Application applyFixed(Member member, Recruit recruit) {
        return Application.builder()
                .member(member)
                .recruit(recruit)
                .priceOffer(recruit.getPrice())
                .priceReason(null)
                .build();
    }

    /** OFFER 정책 지원용 팩토리 (지원자가 가격/사유 입력) */
    public static Application applyOffer(Member member, Recruit recruit, String priceOffer, String priceReason) {
        return Application.builder()
                .member(member)
                .recruit(recruit)
                .priceOffer(priceOffer)
                .priceReason(priceReason)
                .build();
    }

    public void accept() {
        this.status = ApplicationStatus.ACCEPTED;
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    @PrePersist
    @PreUpdate
    private void validateByPricePolicy() {
        PricePolicy policy = recruit.getPricePolicy();
        if (policy == PricePolicy.FIXED) {
            if (isBlank(recruit.getPrice())) {
                throw new NotBlankPriceException();
            }
        } else if (policy == PricePolicy.OFFER) {
            if (isBlank(this.priceOffer)) {
                throw new OfferRequiredException();
            }
            if (isBlank(this.priceReason)) {
                throw new OfferRequiredException();
            }
        } else {
            throw new NotValidPricePolicyException();
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}