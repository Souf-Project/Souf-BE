package com.souf.soufwebsite.global.redis.util;

import com.souf.soufwebsite.domain.feed.service.FeedScheduledService;
import com.souf.soufwebsite.domain.recruit.service.RecruitScheduledService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmUpRunner implements ApplicationRunner {

    private final FeedScheduledService feedScheduledService;
    private final RecruitScheduledService recruitScheduledService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        feedScheduledService.refreshPopularFeeds();
        recruitScheduledService.refreshPopularRecruits();
        log.info("인기 게시글 캐싱 완료");
    }
}
