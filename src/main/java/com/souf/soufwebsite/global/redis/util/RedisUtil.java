package com.souf.soufwebsite.global.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Long> redisTemplate;

    public void set(String key) {
        redisTemplate.opsForValue().set(key, 0L);
    }

    public Long get(String key) {
        return redisTemplate.opsForValue().get(key);
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
