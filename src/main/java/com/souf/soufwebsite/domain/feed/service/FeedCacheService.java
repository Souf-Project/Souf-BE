package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedSummaryDto;
import com.souf.soufwebsite.domain.feed.dto.req.FeedSearchReqDto;
import com.souf.soufwebsite.domain.feed.dto.res.FeedDetailBaseResDto;
import com.souf.soufwebsite.domain.feed.dto.res.FeedSimpleBaseResDto;
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
import com.souf.soufwebsite.global.common.sort.dto.CachePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final CacheManager cacheManager;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final FeedConverter feedConverter;

    private final StringRedisTemplate stringRedisTemplate;

    private final FileService fileService;

    private static final String CACHE_FEED_LIST = "feedList";
    private static final String CACHE_FEED_DETAIL = "feedDetail";
    public static final String TOTAL_HASH = "feed:views:total:";

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
    public FeedDetailBaseResDto getFeedDetailBase(Long memberId, Long feedId) {

        // 피드 소유자
        Member member = findIfMemberIdExists(memberId);
        Feed feed = findIfFeedExist(feedId);

        MemberSummaryDto memberSummaryDto = MemberSummaryDto.of(member);
        FeedSummaryDto feedSummaryDto = FeedSummaryDto.from(feed);

        List<Media> mediaList = fileService.getMediaList(PostType.FEED, feedId);
        List<MediaResDto> mediaResDtos = mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(toList());

        String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return FeedDetailBaseResDto.from(memberSummaryDto, profileImageUrl, feedSummaryDto, mediaResDtos);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = CACHE_FEED_LIST,
            key = "T(java.util.Objects).hash(#reqDto, #pageable.pageNumber, #pageable.pageSize, #pageable.sort)",
            unless = "#result == null"
    )
    public CachePage<FeedSimpleBaseResDto> getFeedsBase(FeedSearchReqDto reqDto, Pageable pageable) {
        Page<Feed> feeds = feedRepository.getFeedList(reqDto, pageable);

        if (feeds.isEmpty()) {
            return new CachePage<>(List.of(), 0, 0, 0, "");
        }

        List<Feed> feedList = feeds.getContent();
        Collection<Object> keys = feedList.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(toList());

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

        List<FeedSimpleBaseResDto> list = feedList.stream()
                .map(feed -> {

                    List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
                    Media m = null;
                    if(!mediaList.isEmpty()) {
                        m = mediaList.get(0);
                    }
                    MediaResDto mediaResDto = m != null ? MediaResDto.fromFeedList(m) : null;

                    Member writer = feed.getMember();
                    String writerProfileUrl = fileService.getMediaUrl(PostType.PROFILE, writer.getId());

                    MemberSummaryDto memberSummaryDto = MemberSummaryDto.of(writer);

                    return FeedSimpleBaseResDto.of(
                            memberSummaryDto,
                            writerProfileUrl,
                            feed,
                            viewCountById.getOrDefault(feed.getId(), feed.getViewCount()),
                            mediaResDto);
                        }
                )
                .toList();

        return new CachePage<>(list,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                feeds.getTotalElements(),
                pageable.getSort().toString());
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfMemberIdExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
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


}
