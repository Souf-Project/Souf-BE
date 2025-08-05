package com.souf.soufwebsite.domain.comment.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long writerId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private Long postId;

    @Column
    private Long commentGroup; // 대댓글을 위한 댓글 ID

    @Column(nullable = false)
    private final String isDeleted = "no";

    public Comment(Long writer, String content, Long author, Long postId, Long commentGroup) {
        this.writerId = writer;
        this.content = content;
        this.authorId = author;
        this.postId = postId;
        this.commentGroup = commentGroup;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
