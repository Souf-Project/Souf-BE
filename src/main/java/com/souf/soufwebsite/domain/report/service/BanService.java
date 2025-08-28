package com.souf.soufwebsite.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BanService {

    private final StringRedisTemplate redis;
    private static String key(long memberId) {
        return "ban:member:%d".formatted(memberId);
    }

    public boolean isBanned(long memberId) {
        return redis.hasKey(key(memberId));
    }

    public void issueTempBan(long memberId, Duration d, String reason) {
        redis.opsForValue().set(key(memberId), "TEMP:" + reason, d);
    }

    public void issuePermBan(long memberId, String reason) {
        redis.opsForValue().set(key(memberId), "PERM:" + reason);
    }

    public Optional<Duration> remaining(long userId) {
        Long sec = redis.getExpire(key(userId), TimeUnit.SECONDS);
        return (sec == null || sec < 0) ? Optional.empty() : Optional.of(Duration.ofSeconds(sec));
    }
    public void lift(long userId) { redis.delete(key(userId)); }
}
