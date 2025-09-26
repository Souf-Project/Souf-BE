package com.souf.soufwebsite.global.redis.service;

import com.souf.soufwebsite.domain.feed.service.FeedScheduledService;
import com.souf.soufwebsite.domain.recruit.service.RecruitScheduledService;
import com.souf.soufwebsite.domain.review.service.ReviewScheduledService;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import com.souf.soufwebsite.global.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class DistributedLockService {

    private final RedissonClient redissonClient;
    private final FeedScheduledService feedScheduledService;
    private final RecruitScheduledService recruitScheduledService;
    private final ViewCountService viewCountService;
    private final ReviewScheduledService reviewScheduledService;
    private final SlackService slackService;

    /* ------------------------------ Feed --------------------------------------- */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void syncFeedView(){
        distributedLock("sync:total:feed:lock", feedScheduledService::syncTotalViewCountsToDB);
    }

    @Scheduled(cron = "0 5 0 * * 1")
    public void syncFeedWeeklyView(){
        distributedLock("sync:feed:lock", feedScheduledService::syncWeeklyViewCountsToDB);
        distributedLock("sync:popular:feed:lock", feedScheduledService::refreshPopularFeeds);
    }

    /* --------------------------------- Recruit --------------------------------- */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void syncRecruitView(){
        distributedLock("sync:recruit:lock", recruitScheduledService::syncViewCountsToDB);
        distributedLock("sync:recruit:popular:lock", recruitScheduledService::refreshPopularRecruits);
    }

    /* --------------------------------- Review ---------------------------------- */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void syncReviewView(){
        distributedLock("sync:review:lock", reviewScheduledService::syncViewCountsToDB);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void initMainCount(){
        distributedLock("init:count:main:lock", viewCountService::initiateRedisKey);
    }

    @Scheduled(cron = "0 */20 * * * *", zone = "Asia/Seoul")
    public void updateMainCount() {
        distributedLock("update:count:main:lock", viewCountService::refreshViewCountCache);
    }

    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    public void syncUpdatedRecruitStatus(){
        distributedLock("sync:recruit:status:lock", recruitScheduledService::updateRecruitableStatus);
    }

    public void distributedLock(String keyName, Runnable task) {
        RLock lock = redissonClient.getLock(keyName);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(3, 60, TimeUnit.SECONDS);
            if (!isLocked) {
                log.warn("락 획득 실패: {}", keyName);
                return;
            }
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("락 대기 중 인터럽트 발생", e);
            slackService.sendSlackMessage("스케줄링 작업 중 현재 스레드에서 오류가 발생했어요!", "error");
        } catch (Exception e) {
            log.error("스케줄 작업 중 예외 발생", e);
            slackService.sendSlackMessage("스케줄 작업 중 오류가 발생했어요!", "error");
            throw e;
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
