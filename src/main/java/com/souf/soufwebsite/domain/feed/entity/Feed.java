package com.souf.soufwebsite.domain.feed.entity;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}
