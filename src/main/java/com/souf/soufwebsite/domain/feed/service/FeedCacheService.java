package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedSummaryDto;
import com.souf.soufwebsite.domain.feed.dto.res.FeedDetailBaseResDto;
import com.souf.soufwebsite.domain.feed.dto.res.PopularFeedResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSummaryDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final CacheManager cacheManager;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final FeedConverter feedConverter;

    private final ViewCountService viewCountService;
    private final FileService fileService;

    private static final String CACHE_FEED_DETAIL = "feedDetail";

    @Transactional(readOnly = true)
    public void refreshPopularFeeds() {
        Cache cache = cacheManager.getCache("popularFeeds");
        if (cache == null) {
            log.error("popularFeeds 캐시가 설정되지 않았습니다.");
            return;
        }

        List<Feed> popularFeeds = feedRepository.findTop6ByOrderByWeeklyViewCountDesc();
        List<PopularFeedResDto> result = popularFeeds.stream()
                .map(feedConverter::getFeedSimpleResDto)
                .toList();
        cache.put("feed:popular", result);

        log.info("인기 피드 캐싱 완료");
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = CACHE_FEED_DETAIL,
            key = "'feedId:' + #feedId",
            unless = "#result == null"
    )
    public FeedDetailBaseResDto getFeedDetailBase(Member currentM, Long memberId, Long feedId, String ip, String userAgent) {

        // 피드 소유자
        Member member = findIfMemberIdExists(memberId);
        Feed feed = findIfFeedExist(feedId);

        MemberSummaryDto memberSummaryDto = MemberSummaryDto.of(member);
        FeedSummaryDto feedSummaryDto = FeedSummaryDto.from(feed);

        Long totalViewCount = viewCountService.updateTotalViewCount(currentM, PostType.FEED, feedId, feed.getViewCount(), ip, userAgent);

        List<Media> mediaList = fileService.getMediaList(PostType.FEED, feedId);
        List<MediaResDto> mediaResDtos = mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(Collectors.toList());

        String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return FeedDetailBaseResDto.from(memberSummaryDto, profileImageUrl, feedSummaryDto, totalViewCount, mediaResDtos);
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfMemberIdExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
    }


}
