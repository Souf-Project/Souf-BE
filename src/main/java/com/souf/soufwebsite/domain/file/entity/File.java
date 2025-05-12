package com.souf.soufwebsite.domain.file.entity;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_Id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String fileUrl;

    @NotNull
    private String fileName;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;


    public static File of(String url, String name, FileType type) {
        return File.builder()
                .fileUrl(url)
                .fileName(name)
                .fileType(type)
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
