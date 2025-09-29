package com.souf.soufwebsite.domain.review.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.exception.NotCompletedTaskException;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.domain.review.dto.ReviewCreatedResDto;
import com.souf.soufwebsite.domain.review.dto.ReviewDetailedResDto;
import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewSimpleResDto;
import com.souf.soufwebsite.domain.review.entity.Review;
import com.souf.soufwebsite.domain.review.exception.NotFoundReviewException;
import com.souf.soufwebsite.domain.review.exception.NotValidReviewAuthentication;
import com.souf.soufwebsite.domain.review.repository.ReviewRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final RecruitRepository recruitRepository;

    private final FileService fileService;
    private final ViewCountService viewCountService;


    private Member getCurrentMember() {
        return SecurityUtils.getCurrentMemberOrNull();
    }

    @Transactional
    @Override
    public ReviewCreatedResDto createReview(String email, ReviewReqDto reviewReqDto) {
        Member currentMember = findIfMemberExists(email);

        Recruit recruit = recruitRepository.findById(reviewReqDto.recruitId()).orElseThrow(NotFoundRecruitException::new);
        if(!recruit.isTaskCompleted()){
            throw new NotCompletedTaskException();
        }

        Review review = new Review(reviewReqDto, recruit, currentMember);
        review = reviewRepository.save(review);

        List<PresignedUrlResDto> presignedUrlResDtos =
                fileService.generatePresignedUrl("review", reviewReqDto.originalFileNames());
        log.info("리뷰가 생성되었습니다! reviewId: {}", review.getId());

        return new ReviewCreatedResDto(review.getId(), presignedUrlResDtos);
    }

    @Override
    public void uploadReviewMedia(String email, MediaReqDto mediaReqDto) {
        findIfMemberExists(email);

        Review review = findIfReviewExists(mediaReqDto.postId());
        fileService.uploadMetadata(mediaReqDto, PostType.REVIEW, review.getId());
    }

    @Override
    public Slice<ReviewSimpleResDto> getReviews(Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public ReviewDetailedResDto getDetailedReview(Long reviewId, String ip, String userAgent) {
        Member currentMember = getCurrentMember();
        Review review = findIfReviewExists(reviewId);
        Recruit recruit = review.getRecruit();

        List<Media> reviewMediaList = fileService.getMediaList(PostType.REVIEW, review.getId());

        long reviewViewTotalCount = viewCountService
                .updateTotalViewCount(currentMember, PostType.REVIEW, review.getId(), review.getViewTotalCount(), ip, userAgent);

        Member member = recruit.getMember();
        String profileUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return ReviewDetailedResDto.from(review, recruit, reviewViewTotalCount, member, profileUrl, reviewMediaList);
    }

    @Transactional
    @Override
    public ReviewCreatedResDto updateReview(String email, Long reviewId, ReviewReqDto reviewReqDto) {
        Member currentMember = findIfMemberExists(email);
        Review review = findIfReviewExists(reviewId);
        verifyIfReviewIsMine(review, currentMember);

        review.updateReview(reviewReqDto);

        updatedRemainingUrls(reviewReqDto, review);
        List<PresignedUrlResDto> presignedUrlResDtos =
                fileService.generatePresignedUrl("review", reviewReqDto.originalFileNames());

        return new ReviewCreatedResDto(review.getId(), presignedUrlResDtos);
    }

    @Override
    public void deleteReview(String email, Long reviewId) {
        Member currentMember = findIfMemberExists(email);
        Review review = findIfReviewExists(reviewId);
        verifyIfReviewIsMine(review, currentMember);

        reviewRepository.delete(review);
    }

    private void verifyIfReviewIsMine(Review review, Member member) {
        log.info("currentMember: {}, feedMember: {}", member, review.getMember());
        if(!review.getMember().getId().equals(member.getId())){
            throw new NotValidReviewAuthentication();
        }
    }

    private Review findIfReviewExists(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(NotFoundReviewException::new);
    }

    private Member findIfMemberExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private void updatedRemainingUrls(ReviewReqDto reqDto, Review review) {
        List<Media> mediaList = fileService.getMediaList(PostType.REVIEW, review.getId());
        for (Media media : mediaList) {
            if (!reqDto.existingImageUrls().contains(media.getOriginalUrl())) {
                fileService.deleteMedia(media);  // DB에서만 삭제되도록 수정
            }
        }
    }
}
