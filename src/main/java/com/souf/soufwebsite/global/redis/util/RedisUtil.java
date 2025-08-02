package com.souf.soufwebsite.global.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key) {
        redisTemplate.opsForValue().set(key, 0L);
    }

    public void setIfAbsent(String key) {
        redisTemplate.opsForValue().setIfAbsent(key, 0L, Duration.ofDays(1));
    }

    public Long get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        return 0L;
    }

    public Set<String> getKeys(String key){
        return redisTemplate.keys(key);
    }

    public void increaseCount(String key){
        redisTemplate.opsForValue().increment(key);
    }

    public void deleteKey(String key){
        redisTemplate.delete(key);
    }

}
