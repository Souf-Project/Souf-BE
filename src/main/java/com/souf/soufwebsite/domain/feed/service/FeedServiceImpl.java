package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.req.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.req.FeedSearchReqDto;
import com.souf.soufwebsite.domain.feed.dto.req.LikeFeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.res.*;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedCategoryMapping;
import com.souf.soufwebsite.domain.feed.entity.LikedFeed;
import com.souf.soufwebsite.domain.feed.event.FeedChangedEvent;
import com.souf.soufwebsite.domain.feed.exception.AlreadyExistsFeedLikeException;
import com.souf.soufwebsite.domain.feed.exception.NotExistsFeedLikeException;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.feed.repository.likedFeed.LikedFeedRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.event.MediaCleanupHelper;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.file.service.MediaCleanupPublisher;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import com.souf.soufwebsite.global.slack.service.SlackService;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final ViewCountService viewCountService;
    private final FeedCacheService feedCacheService;
    private final FeedConverter feedConverter;
//    private final IndexEventPublisherHelper indexEventPublisherHelper;
    private final SlackService slackService;
    private final LikedFeedRepository likedFeedRepository;

    private final MediaCleanupPublisher mediaCleanupPublisher;
    private final MediaCleanupHelper mediaCleanupHelper;

    private final StringRedisTemplate stringRedisTemplate;

    private final ApplicationEventPublisher publisher;

    private static final String CACHE_FEED_LIST = "feedList";
    private static final String CACHE_FEED_DETAIL = "feedDetail";
    public static final String TOTAL_HASH = "feed:views:total:";

    public Member getCurrentMember() {
        return SecurityUtils.getCurrentMemberOrNull();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_FEED_LIST, allEntries = true),
            @CacheEvict(value = CACHE_FEED_DETAIL, allEntries = true),
    })
    public FeedCreatedResDto createFeed(String email, FeedReqDto reqDto) {
        Member member = findIfEmailExists(email);

        Feed feed = Feed.of(reqDto, member);
        injectCategories(reqDto, feed);
        feed = feedRepository.save(feed);

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.FEED,
//                OperationType.CREATE,
//                "Feed",
//                feed
//        );

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());
        VideoDto videoDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.FEED);


        String slackMsg = member.getNickname() + " 님이 피드를 작성하였습니다.\n" +
                "https://www.souf.co.kr/feedDetails/" + feed.getId().toString() + "\n" +
                member.getNickname() + " 님을 다같이 환영해보아요:)";
        slackService.sendSlackMessage(slackMsg, "post");
        return new FeedCreatedResDto(feed.getId(), presignedUrlResDtos, videoDto);
    }


    @Override
    public void uploadFeedMedia(MediaReqDto mediaReqDto) {
        Feed feed = findIfFeedExist(mediaReqDto.postId());
        fileService.uploadMetadata(mediaReqDto, PostType.FEED, feed.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public MemberFeedResDto getStudentFeeds(Long memberId, Pageable pageable) {
        Member member = findIfMemberIdExists(memberId);
        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());
        Page<PopularFeedResDto> feedSimpleResDtos = feedRepository.findAllByMemberOrderByIdDesc(member, pageable)
                .map(feedConverter::getFeedSimpleResDto);

        MemberResDto memberResDto = MemberResDto.from(member, member.getCategories(), mediaUrl, false);
        return new MemberFeedResDto(memberResDto, feedSimpleResDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public FeedDetailResDto getFeedById(Long memberId, Long feedId, String ip, String userAgent) {

        // 현재 사용자
        Member currentMember = getCurrentMember();

        FeedDetailBaseResDto base = feedCacheService.getFeedDetailBase(currentMember, memberId, feedId, ip, userAgent);


        Boolean liked = false;
        if(currentMember != null) {
            liked = getLiked(currentMember.getId(), feedId);
        }

        return FeedDetailResDto.from(base, liked);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_FEED_LIST, allEntries = true),
            @CacheEvict(value = CACHE_FEED_DETAIL, allEntries = true),
            @CacheEvict(value = "competitionTop5", key = "'CURRENT'")
    })
    @Override
    public FeedCreatedResDto updateFeed(String email, Long feedId, FeedReqDto reqDto) {
        Member member = findIfEmailExists(email);
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        feed.updateContent(reqDto);
        updateRemainingUrls(reqDto, feed);

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());
        VideoDto videoDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.FEED);

        feed.clearCategories();
        injectCategories(reqDto, feed);

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.FEED,
//                OperationType.CREATE,
//                "Feed",
//                feed
//        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publisher.publishEvent(
                        new FeedChangedEvent(
                                feed.getId(),
                                FeedChangedEvent.ChangeType.CONTENT_UPDATED
                        )
                );
            }
        });

        return new FeedCreatedResDto(feed.getId(), presignedUrlResDtos, videoDto);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_FEED_LIST, allEntries = true),
            @CacheEvict(value = CACHE_FEED_DETAIL, allEntries = true),
            @CacheEvict(value = "competitionTop5", key = "'CURRENT'")
    })
    @Override
    public void deleteFeed(String email, Long feedId) {
        Member member = findIfEmailExists(email);
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        viewCountService.deleteViewCountFromRedis(PostType.FEED, feedId);

        feedRepository.delete(feed);

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.FEED,
//                OperationType.DELETE,
//                "Feed",
//                feed.getId()
//        );
        mediaCleanupPublisher.publish(PostType.FEED, feedId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publisher.publishEvent(
                        new FeedChangedEvent(
                                feed.getId(),
                                FeedChangedEvent.ChangeType.DELETED
                        )
                );
            }
        });
    }



    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "popularFeeds",
            key = "'feed:popular'")
    public List<PopularFeedResDto> getPopularFeeds() {
        List<Feed> popularFeeds = feedRepository.findTop6ByOrderByWeeklyViewCountDesc();

        log.info("피드 서비스 로직 실행");
        return popularFeeds.stream()
                .map(feedConverter::getFeedSimpleResDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = CACHE_FEED_LIST,
            key = "T(java.util.Objects).hash(#reqDto, #pageable.pageNumber, #pageable.pageSize, #pageable.sort, #root.target.currentMemberIdOrNull())",
            unless = "#result == null"
    )
    @Override
    public Page<FeedSimpleResDto> getFeeds(FeedSearchReqDto reqDto, Pageable pageable) {
        Member viewer = getCurrentMember();
        Long viewerId = (viewer == null) ? null : viewer.getId();

        Page<Feed> feeds = feedRepository.getFeedList(reqDto, pageable);

        if (feeds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0L);
        }

        List<Feed> feedList = feeds.getContent();
        Collection<Object> keys = feedList.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.toList());

        List<Object> redisValues = stringRedisTemplate.opsForHash().multiGet(TOTAL_HASH, keys);

        Map<Long, Long> viewCountById = new HashMap<>(feedList.size() * 2);

        for (int i = 0; i < feedList.size(); i++) {
            Feed f = feedList.get(i);
            Object v = i < redisValues.size() ? redisValues.get(i) : null;

            Long viewCount = parseLongOrNull(v);

            if (viewCount == null) {
                Long db = f.getViewCount();
                viewCount = (db == null) ? 0L : db;
            }

            viewCountById.put(f.getId(), viewCount);
        }

        Set<Long> likedFeedIds = Collections.emptySet();
        if (viewerId != null) {
            List<Long> feedIds = feedList.stream().map(Feed::getId).toList();
            likedFeedIds = new HashSet<>(likedFeedRepository.findLikedFeedIds(viewerId, feedIds));
        }
        final Set<Long> likedSet = likedFeedIds;

        List<FeedSimpleResDto> content = feedList.stream()
                .map(feed -> {
                    List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
                    Media m = null;
                    if(!mediaList.isEmpty()){
                        m = mediaList.get(0);
                    }
                    Member writer = feed.getMember();
                    String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, writer.getId());
                    boolean isLiked = viewerId != null && likedSet.contains(feed.getId());

                    return FeedSimpleResDto.from(writer, profileImageUrl, feed, isLiked, m);
                })
                .toList();

        return new PageImpl<>(content, pageable, feeds.getTotalElements());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_FEED_LIST, allEntries = true),
            @CacheEvict(value = CACHE_FEED_DETAIL, allEntries = true),
            @CacheEvict(value = "competitionTop5", key = "'CURRENT'")
    })
    @Override
    public void updateLikedCount(Long feedId, LikeFeedReqDto likeFeedReqDto) {
        Long memberId = likeFeedReqDto.memberId();

        if (!feedRepository.existsById(feedId)) {
            throw new NotFoundFeedException();
        }

        // 좋아요를 누를 경우
        if(likeFeedReqDto.isLiked().equals(Boolean.TRUE)){
            likedFeedRepository.findByFeedIdAndMemberId(feedId, memberId).ifPresent(likedFeed -> {
                throw new AlreadyExistsFeedLikeException();
            });

            LikedFeed likedFeed = new LikedFeed(memberId, feedId);
            likedFeedRepository.save(likedFeed);

            int updated = feedRepository.incrementLikedCount(feedId);
            if (updated == 0) throw new NotFoundFeedException();

        } else { // 좋아요를 취소할 경우
            likedFeedRepository.findByFeedIdAndMemberId(feedId, memberId).orElseThrow(NotExistsFeedLikeException::new);
            likedFeedRepository.deleteByFeedIdAndMemberId(feedId, memberId);

            int updated = feedRepository.decrementLikedCount(feedId);
            if (updated == 0) {
                log.warn("likedCount decrement skipped. feedId={}, memberId={}", feedId, memberId);
            }
        }
    }

    /* ============================= private method ======================================== */

    private void verifyIfFeedIsMine(Feed feed, Member member) {
        log.info("currentMember: {}, feedMember: {}", member, feed.getMember());
        if(!feed.getMember().getId().equals(member.getId())){
            throw new NotValidAuthenticationException();
        }
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfMemberIdExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
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

    private void updateRemainingUrls(FeedReqDto reqDto, Feed feed) {
        List<String> removed = mediaCleanupHelper.purgeRemovedMedias(
                PostType.FEED,
                feed.getId(),
                reqDto.existingImageUrls()
        );

        // 삭제할 URL이 있으면 S3 삭제 이벤트 발행
        if (!removed.isEmpty()) {
            mediaCleanupPublisher.publishUrls(PostType.FEED, feed.getId(), removed);
        }
    }

    private Long parseLongOrNull(Object v) {
        if (v == null) return null;
        if (v instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        return null;
    }

    @NotNull
    private Boolean getLiked(Long memberId, Long feedId) {
        return likedFeedRepository.existsByFeedIdAndMemberId(feedId, memberId);
    }
}
