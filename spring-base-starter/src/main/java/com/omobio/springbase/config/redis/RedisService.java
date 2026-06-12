package com.omobio.springbase.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String PERMISSION_PREFIX = "permissions:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void cachePermissions(String userId, List<String> permissions) {
        String key = PERMISSION_PREFIX + userId;
        redisTemplate.delete(key);
        if (permissions != null && !permissions.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, permissions);
        }
    }

    public List<String> getPermissionsByUserId(String userId) {
        String key = PERMISSION_PREFIX + userId;
        List<String> permissions = redisTemplate.opsForList().range(key, 0, -1);
        return permissions != null ? permissions : Collections.emptyList();
    }

    public void blacklistToken(String jti, long ttlMillis) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, "1", ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    }
}
