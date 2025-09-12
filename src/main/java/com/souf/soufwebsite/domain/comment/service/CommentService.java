package com.souf.soufwebsite.domain.comment.service;

import com.souf.soufwebsite.domain.comment.dto.CommentReqDto;
import com.souf.soufwebsite.domain.comment.dto.CommentResDto;
import com.souf.soufwebsite.domain.comment.dto.CommentUpdateReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentService {

    void createComment(Long postId, CommentReqDto reqDto);

    void createReply(Long postId, CommentReqDto reqDto);

    void deleteComment(String email, Long postId, Long commentId);

    void updateComment(String email, Long postId, CommentUpdateReqDto reqDto);

    Slice<CommentResDto> getComments(Long postId, Pageable pageable);

    Page<CommentResDto> getReplyComments(Long postId, Long commentId, Pageable pageable);
}
