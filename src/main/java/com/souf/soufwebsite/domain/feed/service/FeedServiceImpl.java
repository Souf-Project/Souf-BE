package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedCategoryMapping;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final RedisUtil redisUtil;
    private final FeedConverter feedConverter;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    @Transactional
    public FeedResDto createFeed(FeedReqDto reqDto) {
        Member member = getCurrentUser();

        Feed feed = Feed.of(reqDto, member);
        injectCategories(reqDto, feed);
        feed = feedRepository.save(feed);

        String feedViewKey = getFeedViewKey(feed.getId());
        redisUtil.set(feedViewKey);

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());
        VideoResDto videoResDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.FEED);

        return new FeedResDto(feed.getId(), presignedUrlResDtos, videoResDto);
    }

    @Override
    public void uploadFeedMedia(MediaReqDto mediaReqDto) {
        Feed feed = findIfFeedExist(mediaReqDto.postId());
        List<Media> mediaList = fileService.uploadMetadata(mediaReqDto, PostType.FEED, feed.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public MemberFeedResDto getStudentFeeds(Long memberId, Pageable pageable) {
        Member member = findIfMemberExists(memberId);
        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());
        Page<FeedSimpleResDto> feedSimpleResDtos = feedRepository.findAllByMemberOrderByIdDesc(member, pageable)
                .map(feedConverter::getFeedSimpleResDto);

        MemberResDto memberResDto = MemberResDto.from(member, member.getCategories(), mediaUrl);
        return new MemberFeedResDto(memberResDto, feedSimpleResDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public FeedDetailResDto getFeedById(Long memberId, Long feedId) {
        Member member = findIfMemberExists(memberId);
        Feed feed = findIfFeedExist(feedId);

        String feedViewKey = getFeedViewKey(feed.getId());
        redisUtil.increaseCount(feedViewKey);
        Long viewCountFromRedis = redisUtil.get(feedViewKey);

        List<Media> mediaList = fileService.getMediaList(PostType.FEED, feedId);
        String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return FeedDetailResDto.from(member, profileImageUrl, feed, viewCountFromRedis, mediaList);
    }

    @Transactional
    @Override
    public FeedResDto updateFeed(Long feedId, FeedReqDto reqDto) {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        feed.updateContent(reqDto);
        updatedRemainingUrls(reqDto, feed);

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());
        VideoResDto videoResDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.FEED);

        feed.clearCategories();
        injectCategories(reqDto, feed);

        return new FeedResDto(feed.getId(), presignedUrlResDtos, videoResDto);
    }

    @Override
    public void deleteFeed(Long feedId) {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        String feedViewKey = getFeedViewKey(feed.getId());
        redisUtil.deleteKey(feedViewKey);

        feedRepository.delete(feed);
    }

    @Override
    @Cacheable(value = "popularFeeds", key = "'page:' + #pageable.pageNumber")
    public Page<FeedSimpleResDto> getPopularFeeds(Pageable pageable) {
        Page<Feed> popularFeeds = feedRepository.findByOrderByViewCountDesc(pageable);

         return popularFeeds.map(
                 feedConverter::getFeedSimpleResDto
        );
    }

    @Override
    public Slice<FeedDetailResDto> getFeeds(Long first, Pageable pageable) {
        Slice<Feed> feeds = feedRepository.findByFirstCategoryOrderByCreatedTimeDesc(first, pageable);

        return feeds.map(
                feed -> {
                    String feedViewKey = getFeedViewKey(feed.getId());
                    Long viewCountFromRedis = redisUtil.get(feedViewKey);
                    List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
                    Member member = feed.getMember();
                    String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

                    return FeedDetailResDto.from(feed.getMember(), profileImageUrl, feed, viewCountFromRedis, mediaList);
                }
        );
    }

    private void verifyIfFeedIsMine(Feed feed, Member member) {
        log.info("currentMember: {}, feedMember: {}", member, feed.getMember());
        if(!feed.getMember().getId().equals(member.getId())){
            throw new NotValidAuthenticationException();
        }
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundFeedException::new);
    }

    private String getFeedViewKey(Long feedId) {
        return "feed:view:" + feedId;
    }

    private void injectCategories(FeedReqDto reqDto, Feed feed) {
        for(CategoryDto dto : reqDto.categoryDtos()){
            FirstCategory firstCategory = categoryService.findIfFirstIdExists(dto.firstCategory());
            SecondCategory secondCategory = categoryService.findIfSecondIdExists(dto.secondCategory());
            ThirdCategory thirdCategory = categoryService.findIfThirdIdExists(dto.thirdCategory());

            categoryService.validate(dto.firstCategory(), dto.secondCategory(), dto.thirdCategory());

            FeedCategoryMapping recruitCategoryMapping = FeedCategoryMapping.of(feed, firstCategory, secondCategory, thirdCategory);
            feed.addCategory(recruitCategoryMapping);
        }
    }

    private void updatedRemainingUrls(FeedReqDto reqDto, Feed feed) {
        List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
        for (Media media : mediaList) {
            if (!reqDto.existingImageUrls().contains(media.getOriginalUrl())) {
                fileService.deleteMedia(media);  // DB에서만 삭제되도록 수정
            }
        }
    }
}
