package com.souf.soufwebsite.domain.comment.service;

import com.souf.soufwebsite.domain.comment.dto.CommentReqDto;
import com.souf.soufwebsite.domain.comment.dto.CommentResDto;
import com.souf.soufwebsite.domain.comment.dto.CommentUpdateReqDto;
import com.souf.soufwebsite.domain.comment.entity.Comment;
import com.souf.soufwebsite.domain.comment.exception.NotFoundCommentException;
import com.souf.soufwebsite.domain.comment.exception.NotMatchedOwnerException;
import com.souf.soufwebsite.domain.comment.repository.CommentRepository;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    public void createComment(Long postId, CommentReqDto reqDto) {

        Member writer = findIfMemberExists(reqDto.writerId());
        Member author = findIfMemberExists(reqDto.authorId());

        Feed feed = findIfFeedExist(postId);

        Long parent = commentRepository.nextCommentGroup(feed); // 다음 댓글 그룹을 지정
        Comment comment = new Comment(writer, reqDto.content(),
                author.getId(), feed, parent);
        commentRepository.save(comment);
        log.info("{} 피드에 대한 댓글 생성 완료", feed.getId());
    }

    @Override
    public void createReply(Long postId, CommentReqDto reqDto) {
        Comment parentComment = findIfCommentExists(reqDto.parentId());

        Member writer = findIfMemberExists(reqDto.writerId());
        Member author = findIfMemberExists(reqDto.authorId());

        Feed feed = findIfFeedExist(postId);

        Comment comment = new Comment(writer, reqDto.content(),
                author.getId(), feed, parentComment.getCommentGroup());
        commentRepository.save(comment);
        log.info("{}에 대한 대댓글 생성", reqDto.parentId());
    }

    @Transactional
    @Override
    public void deleteComment(Long postId, Long commentId) {
        Member member = getCurrentUser();
        Comment comment = findIfCommentExists(commentId);

        validatedIfCommentMine(member, comment); // 현재 사용자와 댓글 작성자의 아이디가 일치하지 않으면 예외 발생

        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public void updateComment(Long postId, CommentUpdateReqDto reqDto) {
        Member member = getCurrentUser();
        Comment comment = findIfCommentExists(reqDto.commentId());

        validatedIfCommentMine(member, comment); // 현재 사용자와 댓글 작성자의 아이디가 일치하지 않으면 예외 발생

        comment.updateContent(reqDto.content());
    }

    @Override
    public Slice<CommentResDto> getComments(Long postId, Pageable pageable) {
        findIfFeedExist(postId);
        Slice<Comment> comments = commentRepository.findFirstByGroup(postId, pageable);

        List<CommentResDto> commentResDtos = comments.stream().map(
                comment -> {
                    String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, comment.getWriter().getId());
                    return CommentResDto.from(comment, comment.getWriter(), mediaUrl);
                }
        ).toList();

        return new SliceImpl<>(
                commentResDtos,
                pageable,
                comments.hasNext()
        );
    }

    @Override
    public Page<CommentResDto> getReplyComments(Long postId, Long commentId, Pageable pageable) {
        Feed feed = findIfFeedExist(postId);

        Page<Comment> replyComments = commentRepository
                .findByFeedAndCommentGroupOrderByCreatedTime(feed, commentId, pageable);


        List<CommentResDto> commentResDtos = replyComments.stream().map(
                comment -> {
                    String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, comment.getWriter().getId());
                    return CommentResDto.from(comment, comment.getWriter(), mediaUrl);
                }
        ).toList();

        return new PageImpl<>(commentResDtos, pageable, replyComments.getTotalPages());
    }


    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundFeedException::new);
    }

    private Comment findIfCommentExists(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NotFoundCommentException::new);
    }

    private static void validatedIfCommentMine(Member member, Comment comment) {
        if(!comment.getWriter().getId().equals(member.getId())){
            throw new NotMatchedOwnerException();
        }
    }
}
