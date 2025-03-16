package com.souf.soufwebsite.domain.feed.entity;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedId")
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Builder
    public Feed(FeedReqDto reqDto, User user) {
        this.content = reqDto.content();
        this.user = user;
    }

    public static Feed of(FeedReqDto reqDto, User user) {
        return Feed.builder()
                .reqDto(reqDto)
                .user(user)
                .build();
    }

    public void updateFeed(FeedReqDto reqDto) {
        this.content = reqDto.content();
    }
}
