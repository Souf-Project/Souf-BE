package com.souf.soufwebsite.domain.file.entity;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Media extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_Id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String originalUrl;

    // @NotNull
    // @Column
    // private String thumbnailUrl;

    // @NotNull
    // private String detailThumbnailUrl;

    @NotNull
    private String fileName;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;


    public static Media of(String url, String name, MediaType type) {
        return Media.builder()
                .originalUrl(url)
                .fileName(name)
                .mediaType(type)
                .build();
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public void assignToRecruit(Recruit recruit) {
        this.recruit = recruit;
    }

    public void assignToFeed(Feed feed){
        this.feed = feed;
    }
}
