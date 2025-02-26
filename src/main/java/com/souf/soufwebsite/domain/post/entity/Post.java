package com.souf.soufwebsite.domain.post.entity;

import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 1, max = 100)
    private String title;

    @NotNull
    @Lob
    @Column(nullable = false)
    private String content;

    @Size(max = 50)
    private String region;

    // 마감일자
    private LocalDateTime deadline;

    // 작성자 (userId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

}