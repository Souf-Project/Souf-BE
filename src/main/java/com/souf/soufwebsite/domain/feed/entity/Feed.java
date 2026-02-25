package com.souf.soufwebsite.domain.feed.entity;

import com.souf.soufwebsite.domain.comment.entity.Comment;
import com.souf.soufwebsite.domain.feed.dto.req.FeedReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String topic;

    @NotNull
    @Column(length = 300, nullable = false)
    private String content;

    @NotNull
    @Column(nullable = false)
    private Long viewCount;

    @NotNull
    @Column(nullable = false)
    private Long weeklyViews;

    @Column(nullable = false)
    private int likedCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(name = "backup_topic")
    private String backupTopic;

    @Column(name = "backup_content", length = 300)
    private String backupContent;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedCategoryMapping> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Feed(String topic, String content, Member member) {
        this.topic = topic;
        this.content = content;
        this.member = member;
        this.viewCount = 0L;
        this.weeklyViews = 0L;
    }

    public static Feed of(FeedReqDto createReqDto, Member member) {
        return Feed.builder()
                .topic(createReqDto.topic())
                .content(createReqDto.content())
                .member(member)
                .build();
    }

    public void updateContent(FeedReqDto reqDto) {
        this.topic = reqDto.topic();
        this.content = reqDto.content();
    }

    public void addViewCount(Long count){
        this.viewCount += count;
    }

    public void addCategory(FeedCategoryMapping feedCategoryMapping){
        this.categories.add(feedCategoryMapping);
    }

    public void clearCategories() {
        for (FeedCategoryMapping mapping : categories) {
            mapping.disconectFeed();
        }
        categories.clear();
    }

    public void softDeleteByOwner() {
        this.isDeleted = true;
        this.topic = "삭제된 게시글";
        this.content = "탈퇴한 회원의 게시글입니다.";
        this.clearCategories();
    }

    public void softDeleteByAdmin(String reason) {
        if (this.isDeleted) return;

        // 원문 백업 (1회만)
        this.backupTopic = this.topic;
        this.backupContent = this.content;

        // 화면에는 삭제 문구
        this.isDeleted = true;
        this.topic = "관리자에 의해 삭제된 게시글";
        this.content = (reason == null || reason.isBlank())
                ? "운영 정책에 의해 삭제된 게시글입니다."
                : "운영 정책에 의해 삭제된 게시글입니다.\n사유: " + reason;

        this.clearCategories();
    }

    public void restoreByAdmin() {
        if (!this.isDeleted) return;

        // 백업이 있으면 원문 복구
        if (this.backupTopic != null) this.topic = this.backupTopic;
        if (this.backupContent != null) this.content = this.backupContent;

        // 상태 복구 + 백업 제거
        this.isDeleted = false;
        this.backupTopic = null;
        this.backupContent = null;
    }
}
