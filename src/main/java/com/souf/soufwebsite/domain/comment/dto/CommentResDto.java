package com.souf.soufwebsite.domain.comment.dto;

import com.souf.soufwebsite.domain.comment.entity.Comment;
import com.souf.soufwebsite.domain.member.entity.Member;

import java.time.LocalDateTime;

public record CommentResDto(
        Long commentId,
        Long writerId,
        String nickname,
        String content,
        String profileUrl,
        LocalDateTime lastModifiedTime
) {
    public static CommentResDto from(Comment comment, Member writer, String mediaUrl) {
        return new CommentResDto(
                comment.getId(),
                writer.getId(),
                writer.getNickname(),
                comment.getContent(),
                mediaUrl,
                comment.getLastModifiedTime()
        );
    }
}
