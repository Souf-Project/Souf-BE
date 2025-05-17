package com.souf.soufwebsite.domain.feed.entity;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.file.entity.File;
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

    @Lob
    @NotNull
    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedTag> feedTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();


    @Builder
    private Feed(String topic, String content, Member member) {
        this.topic = topic;
        this.content = content;
        this.member = member;
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

    // 연관관계 편의 메서드
    public void addFileOnFeed(File file){
        this.files.add(file);
        file.assignToFeed(this);
    }

    public void addFeedTagOnFeed(FeedTag feedTag){
        this.feedTags.add(feedTag);
    }

    public void removeFile(File file) {
        files.remove(file);
        file.setFeed(this);
    }
}
