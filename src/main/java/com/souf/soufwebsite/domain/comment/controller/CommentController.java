package com.souf.soufwebsite.domain.comment.controller;

import com.souf.soufwebsite.domain.comment.dto.CommentReqDto;
import com.souf.soufwebsite.domain.comment.dto.CommentResDto;
import com.souf.soufwebsite.domain.comment.dto.CommentUpdateReqDto;
import com.souf.soufwebsite.domain.comment.service.CommentService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.comment.controller.CommentSuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/{postId}/comment")
public class CommentController implements CommentApiSpecification{

    private final CommentService commentService;

    @PostMapping
    public SuccessResponse createComment(
            @PathVariable(name = "postId") Long postId,
            @Valid @RequestBody CommentReqDto reqDto) {

        commentService.createComment(postId, reqDto);

        return new SuccessResponse(CREATE_COMMENT_SUCCESS.getMessage());
    }

    @PostMapping("/reply")
    public SuccessResponse createReply(
            @PathVariable(name = "postId") Long postId,
            @Valid @RequestBody CommentReqDto reqDto) {

        commentService.createReply(postId, reqDto);

        return new SuccessResponse(CREATE_COMMENT_SUCCESS.getMessage());
    }

    @GetMapping
    public SuccessResponse<Slice<CommentResDto>> getComments(
            @PathVariable(name = "postId") Long postId,
            @PageableDefault Pageable pageable) {

        Slice<CommentResDto> comments = commentService.getComments(postId, pageable);

        return new SuccessResponse<>(comments, READ_COMMENTS_SUCCESS.getMessage());
    }

    @GetMapping("/{commentId}/reply")
    public SuccessResponse<Page<CommentResDto>> getReplyComments(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") Long commentId,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<CommentResDto> replyComments = commentService.getReplyComments(postId, commentId, pageable);
        return new SuccessResponse<>(replyComments, READ_COMMENTS_SUCCESS.getMessage());
    }

    @PatchMapping
    public SuccessResponse updateComment(
            @PathVariable(name = "postId") Long postId,
            @Valid @RequestBody CommentUpdateReqDto reqDto) {
        commentService.updateComment(postId, reqDto);

        return new SuccessResponse(UPDATE_COMMENT_SUCCESS.getMessage());
    }

    @DeleteMapping
    public SuccessResponse deleteComment(
            @PathVariable(name = "postId") Long postId,
            @Valid @RequestBody CommentUpdateReqDto reqDto) {

        commentService.deleteComment(postId, reqDto);

        return new SuccessResponse(DELETE_COMMENT_SUCCESS.getMessage());
    }
}
