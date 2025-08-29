package com.souf.soufwebsite.domain.report.entity;

import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(nullable = false)
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportReasonMapping> reportReasonMappings = new LinkedHashSet<>();

    @Column(nullable = false)
    private String description;

    /** 신고자 (필수) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    /** 신고당한 회원 (대상이 회원인 경우 세팅) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_member_id")
    private Member reportedMember;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column(nullable = false)
    private Long postId;

    @Column
    private String postTitle;

    public Report(String description, Member reportingPerson, Member reportedPerson, PostType postType, Long postId, String postTitle) {
        this.description = description;
        this.reporter = reportingPerson;
        this.reportedMember = reportedPerson;
        this.postType = postType;
        this.postId = postId;
        this.postTitle = postTitle;
        this.status = ReportStatus.PENDING;
    }

    public void addReportReasonMapping(ReportReasonMapping reportReasonMapping) {
        this.reportReasonMappings.add(reportReasonMapping);
    }

    public void updateStatus(ReportStatus status) {
        this.status = status;
    }
}
