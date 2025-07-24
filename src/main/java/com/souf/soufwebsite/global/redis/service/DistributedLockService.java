package com.souf.soufwebsite.global.redis.service;

import com.souf.soufwebsite.domain.feed.service.FeedScheduledService;
import com.souf.soufwebsite.domain.recruit.service.RecruitScheduledService;
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
    private final SlackService slackService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void syncFeedView(){
        distributedLock("sync:feed:lock", feedScheduledService::syncViewCountsToDB);
        distributedLock("sync:popular:feed:lock", feedScheduledService::refreshPopularFeeds);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void syncRecruitView(){
        distributedLock("sync:recruit:lock", recruitScheduledService::syncViewCountsToDB);
        distributedLock("sync:recruit:popular:lock", recruitScheduledService::refreshPopularRecruits);
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
            slackService.sendSlackMessage("스케줄 작업 중 오류가 발생했어요!", "error");
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
