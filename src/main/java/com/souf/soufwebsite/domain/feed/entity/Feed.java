package com.souf.soufwebsite.domain.feed.entity;

import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
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

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();


    @Builder
    private Feed(String content, User user, List<File> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("파일이 최소 1개 이상 필요합니다.");
        }
        this.content = content;
        this.user = user;
        files.forEach(this::addFile);
    }

    public static Feed of(String content, User user, List<File> files) {
        return Feed.builder()
                .content(content)
                .user(user)
                .files(files)
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    // 연관관계 편의 메서드
    public void addFile(File file) {
        files.add(file);
        file.setFeed(this);
    }

    public void removeFile(File file) {
        files.remove(file);
        file.setFeed(this);
    }
}
