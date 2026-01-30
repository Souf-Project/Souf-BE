package com.souf.soufwebsite.domain.comment.service;

import com.souf.soufwebsite.domain.comment.dto.CommentReqDto;
import com.souf.soufwebsite.domain.comment.dto.CommentResDto;
import com.souf.soufwebsite.domain.comment.dto.CommentUpdateReqDto;
import com.souf.soufwebsite.domain.comment.entity.Comment;
import com.souf.soufwebsite.domain.comment.exception.*;
import com.souf.soufwebsite.domain.comment.repository.CommentRepository;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import com.souf.soufwebsite.domain.notification.event.NotificationEvent;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void createComment(String email, Long postId, CommentReqDto reqDto) {

        Member writer = findIfEmailExists(email);
        Feed feed = findIfFeedExist(postId);
        Long authorId = feed.getMember().getId();

        Comment comment = new Comment(writer, reqDto.content(), authorId, feed);
        Comment saved = commentRepository.saveAndFlush(comment);
        saved.updateCommentGroup(saved.getId());

        if (!authorId.equals(writer.getId())) {
            eventPublisher.publishEvent(new NotificationEvent(
                    authorId,
                    NotificationType.FEED_COMMENT_CREATED,
                    "새 댓글 알림",
                    writer.getNickname() + " : " + trim(saved.getContent()),
                    "COMMENT",
                    feed.getId(),
                    "FEED_COMMENT_CREATED:COMMENT:" + saved.getId()
            ));
        }

        int updated = feedRepository.incrementCommentCount(postId);
        if (updated == 0) {
            throw new NotFoundFeedException();
        }
        log.info("{} 피드에 대한 댓글 생성 완료", feed.getId());
    }

    @Transactional
    @Override
    public void createReply(String email, Long postId, CommentReqDto reqDto) {
        Comment parentComment = findIfCommentExists(reqDto.parentId());

        validateCommentAndFeed(parentComment.getFeed().getId(), postId);
        validateReplyToReply(parentComment.getId(), parentComment.getCommentGroup());

        Member writer = findIfEmailExists(email);
        Feed feed = parentComment.getFeed();
        Long authorId = feed.getMember().getId();

        Comment comment = new Comment(writer, reqDto.content(), authorId, feed);
        comment.updateCommentGroup(parentComment.getCommentGroup());
        Comment saved = commentRepository.save(comment);

        Long parentWriterId = parentComment.getWriter().getId();
        if (!parentWriterId.equals(writer.getId())) {
            eventPublisher.publishEvent(new NotificationEvent(
                    parentWriterId,
                    NotificationType.FEED_REPLY_CREATED,
                    "대댓글 알림",
                    writer.getNickname() + " : " + trim(saved.getContent()),
                    "REPLY",
                    parentComment.getId(),
                    "FEED_REPLY_CREATED:COMMENT:" + saved.getId()
            ));
        }

        int updated = feedRepository.incrementCommentCount(postId);
        if (updated == 0) {
            throw new NotFoundFeedException();
        }
        log.info("{}에 대한 대댓글 생성", reqDto.parentId());
    }

    @Transactional
    @Override
    public void deleteComment(String email, Long postId, Long commentId) {
        Member member = findIfEmailExists(email);
        Comment comment = findIfCommentExists(commentId);

        validatedIfCommentMine(member, comment); // 현재 사용자와 댓글 작성자의 아이디가 일치하지 않으면 예외 발생
        validateCommentAndFeed(comment.getFeed().getId(), postId);

        Long group = comment.getCommentGroup();
        // 혹시 null일 경우(방어 코드)
        if (group == null) { // 방어
            commentRepository.delete(comment);
            feedRepository.decrementCommentCount(postId);
            return;
        }

        boolean isParent = comment.getId().equals(group);

        if (isParent) {
            // 부모 삭제
            boolean hasReplies = commentRepository.existsByFeedIdAndCommentGroupAndIdNot(postId, group, comment.getId());

            if (!hasReplies) {
                // 대댓글 없으면 하드 삭제
                commentRepository.delete(comment);

                int updated = feedRepository.decrementCommentCount(postId);
                if (updated == 0) {
                    log.info("대댓글이 없는 댓글 삭제 실패: 피드 아이디 {}", postId);
                    throw new NotExistsDeletedComment();
                }
                return;
            }

            // 대댓글 있으면 부모는 마스킹(soft delete)
            comment.softDelete();
            // count는 유지(정책)
            return;
        }

        // 대댓글 삭제
        commentRepository.delete(comment);

        int updated = feedRepository.decrementCommentCount(postId);
        if (updated == 0) {
            log.info("대댓글 삭제 실패: 피드 아이디 {}", postId);
            throw new NotExistsDeletedComment();
        }

        // 대댓글 삭제 후 group에 부모만 남았고, 부모가 soft delete면 부모도 정리
        long remain = commentRepository.countByFeedIdAndCommentGroup(postId, group);
        if (remain == 1) {
            Comment parent = commentRepository.findById(group).orElse(null);
            if (parent != null && parent.isDeleted()) {
                commentRepository.delete(parent);

                int updated2 = feedRepository.decrementCommentCount(postId);
                if (updated2 == 0) {
                    log.info("부모 댓글 정리 삭제 실패: 피드 아이디 {}", postId);
                    throw new NotExistsDeletedComment();
                }
            }
        }
    }

    @Transactional
    @Override
    public void updateComment(String email, Long postId, CommentUpdateReqDto reqDto) {
        Member member = findIfEmailExists(email);
        Comment comment = findIfCommentExists(reqDto.commentId());

        validatedIfCommentMine(member, comment); // 현재 사용자와 댓글 작성자의 아이디가 일치하지 않으면 예외 발생
        validateCommentAndFeed(comment.getFeed().getId(), postId);

        comment.updateContent(reqDto.content());
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public Page<CommentResDto> getReplyComments(Long postId, Long commentId, Pageable pageable) {
        Feed feed = findIfFeedExist(postId);
        Comment currentComment = findIfCommentExists(commentId);

        validateCommentAndFeed(currentComment.getFeed().getId(), postId);

        Page<Comment> replyComments = commentRepository
                .findRepliesByFeedIdAndGroup(feed.getId(), currentComment.getCommentGroup(), pageable);


        List<CommentResDto> dtos = replyComments.getContent().stream()
                .map(comment -> {
                    String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, comment.getWriter().getId());
                    return CommentResDto.from(comment, comment.getWriter(), mediaUrl);
                }).toList();


        return new PageImpl<>(dtos, pageable, replyComments.getTotalElements());
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Comment findIfCommentExists(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NotFoundCommentException::new);
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private void validateCommentAndFeed(Long feedIdByComment, Long feedId){
        if(!feedIdByComment.equals(feedId)) {
            throw new NotMatchedCommentAndFeedException();
        }
    }

    private void validateReplyToReply(Long commentId, Long commentGroup){
        if(!commentId.equals(commentGroup)) {
            throw new NotReplyToReplyException();
        }
    }

    private static void validatedIfCommentMine(Member member, Comment comment) {
        if(!comment.getWriter().getId().equals(member.getId())){
            throw new NotMatchedOwnerException();
        }
    }

    private String trim(String content) {
        int max = 50;
        return content.length() <= max
                ? content
                : content.substring(0, max) + "...";
    }
}
