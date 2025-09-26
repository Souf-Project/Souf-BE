package com.souf.soufwebsite.domain.review.entity;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Long viewTotalCount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    @Builder
    public Review(ReviewReqDto reqDto, Recruit recruit, Member member) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.score = reqDto.score();
        this.viewTotalCount = 0L;
        this.recruit = recruit;
        this.member = member;
    }

    public void updateReview(ReviewReqDto reqDto) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.score = reqDto.score();
    }
}
