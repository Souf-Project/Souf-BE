package com.souf.soufwebsite.domain.comment.entity;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.member.entity.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Long authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column
    private Long commentGroup; // 대댓글을 위한 댓글 ID

    public Comment(Member writer, String content, Long author, Feed feed) {
        this.writer = writer;
        this.content = content;
        this.authorId = author;
        this.feed = feed;
    }

    public void updateCommentGroup(Long groupId) {
        this.commentGroup = groupId;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    /** 작성자 탈퇴 시 댓글 익명화 */
    public void anonymize() {
        this.content = "탈퇴한 회원의 댓글입니다.";
    }
}
