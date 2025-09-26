package com.souf.soufwebsite.domain.review.service;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.exception.NotCompletedTaskException;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewResDto;
import com.souf.soufwebsite.domain.review.entity.Review;
import com.souf.soufwebsite.domain.review.exception.NotFoundReviewException;
import com.souf.soufwebsite.domain.review.repository.ReviewRepository;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final RecruitRepository recruitRepository;

    private final FileService fileService;

    private Member getCurrentMember() {
        return SecurityUtils.getCurrentMemberOrNull();
    }

    @Override
    public void createReview(String email, ReviewReqDto reviewReqDto) {
        Member currentMember = findIfMemberExists(email);

        Recruit recruit = recruitRepository.findById(reviewReqDto.recruitId()).orElseThrow(NotFoundRecruitException::new);
        if(!recruit.isTaskCompleted()){
            throw new NotCompletedTaskException();
        }

        Review review = new Review(reviewReqDto, recruit, currentMember);
        reviewRepository.save(review);
        log.info("리뷰가 생성되었습니다! reviewId: {}", review.getId());
    }

    @Override
    public ReviewResDto getDetailedReview(Long reviewId) {
        Member member = getCurrentMember();
        Review review = findIfReviewExists(reviewId);
        Recruit recruit = review.getRecruit();

        List<Media> reviewMediaList = fileService.getMediaList(PostType.REVIEW, review.getId());

        String profileUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return ReviewResDto.from(review, recruit, member, profileUrl, reviewMediaList);
    }

    private Review findIfReviewExists(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(NotFoundReviewException::new);
    }

    private Member findIfMemberExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }
}
