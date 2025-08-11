package com.souf.soufwebsite.domain.comment.controller;

import com.souf.soufwebsite.domain.comment.dto.CommentReqDto;
import com.souf.soufwebsite.domain.comment.dto.CommentResDto;
import com.souf.soufwebsite.domain.comment.dto.CommentUpdateReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 관련 API")
public interface CommentApiSpecification {

    @Operation(summary = "댓글 생성", description = "게시글에 대한 댓글을 생성합니다.")
    @PostMapping
    SuccessResponse createComment(
            @PathVariable Long postId,
            @RequestBody CommentReqDto reqDto);

    @Operation(summary = "대댓글 생성", description = "게시글의 댓글에 댓글을 생성합니다.")
    @PostMapping("/reply")
    SuccessResponse createReply(
            @PathVariable(name = "postId") Long postId,
            @RequestBody CommentReqDto reqDto);

    @Operation(summary = "댓글 리스트 조회", description = "댓글 리스트를 조회합니다.")
    @GetMapping
    SuccessResponse<Slice<CommentResDto>> getComments(
            @PathVariable Long postId,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "대댓글 리스트 조회", description = "특정 댓글에 대한 대댓글 리스트를 조회합니다.")
    @GetMapping("/{commentId}/reply")
    SuccessResponse<Page<CommentResDto>> getReplyComments(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") Long commentId,
            @PageableDefault(size = 5) Pageable pageable
    );

    @Operation(summary = "댓글 수정", description = "사용자가 작성한 댓글을 수정합니다.")
    @PatchMapping
    SuccessResponse updateComment(
            @PathVariable Long postId,
            @RequestBody CommentUpdateReqDto reqDto);

    @Operation(summary = "댓글 삭제", description = "사용자가 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    SuccessResponse deleteComment(
            @PathVariable Long postId,
            @PathVariable(name = "commentId") Long commentId);
}
