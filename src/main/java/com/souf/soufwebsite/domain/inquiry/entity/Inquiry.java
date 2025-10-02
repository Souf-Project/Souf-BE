package com.souf.soufwebsite.domain.inquiry.entity;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false)
    private String content;

    @Column(name = "inquiry_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryType inquiryType;

    @Column(name = "inquriy_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryStatus inquiryStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Inquiry (String title, String content, InquiryType inquiryType, Member member) {
        this.title = title;
        this.content = content;
        this.inquiryType = inquiryType;
        this.member = member;
        this.inquiryStatus = InquiryStatus.PENDING;
    }

    public static Inquiry of(InquiryReqDto reqDto, Member member) {
        return Inquiry.builder()
                .title(reqDto.title())
                .content(reqDto.content())
                .inquiryType(reqDto.type())
                .member(member)
                .build();
    }

    public void updateInquiry(InquiryReqDto reqDto) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.inquiryType = reqDto.type();
    }
}
