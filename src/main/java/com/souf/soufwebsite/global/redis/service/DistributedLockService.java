package com.souf.soufwebsite.global.redis.service;

import com.souf.soufwebsite.domain.feed.service.FeedScheduledService;
import com.souf.soufwebsite.domain.recruit.service.RecruitScheduledService;
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

    @Scheduled(cron = "0 0 0 * * *")
    public void syncFeedView(){
        distributedLock("sync:feed:lock", feedScheduledService::syncViewCountsToDB);
        distributedLock("sync:popular:feed:lock", feedScheduledService::refreshPopularFeeds);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void syncRecruitView(){
        distributedLock("sync:recruit:lock", recruitScheduledService::syncViewCountsToDB);
        distributedLock("sync:recruit:popular:lock", recruitScheduledService::refreshPopularRecruits);
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void syncUpdatedRecruitStatus(){
        distributedLock("sync:recruit:status:lock", recruitScheduledService::updateRecruitableStatus);
    }

    public void distributedLock(String keyName, Runnable task) {
        RLock lock = redissonClient.getLock(keyName);

        boolean isLocked = false;
        try{
            isLocked = lock.tryLock(3, 60, TimeUnit.SECONDS);
            if(!isLocked){
                log.warn("락을 획득하지 못했습니다.");
            }
            // 실제 로직 처리
            task.run();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
