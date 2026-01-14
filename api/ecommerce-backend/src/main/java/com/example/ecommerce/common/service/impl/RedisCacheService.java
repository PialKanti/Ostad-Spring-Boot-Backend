package com.example.ecommerce.common.service.impl;

import com.example.ecommerce.common.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link CacheService} using Redis.
 *
 * <p>Provides JSON serialization for cached objects via Jackson
 * ObjectMapper with support for TTL-based expiration.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<T> get(String key, Class<T> valueType) {
        Object cachedValue = getValue(key);
        if (Objects.isNull(cachedValue)) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.convertValue(cachedValue, valueType));
    }

    @Override
    public <T> List<T> getMany(String key, Class<T> itemValueType) {
        Object cachedValue = getValue(key);
        if (Objects.isNull(cachedValue)) {
            return Collections.emptyList();
        }

        return objectMapper.convertValue(cachedValue, objectMapper.getTypeFactory().constructCollectionType(List.class, itemValueType));
    }

    @Override
    public void put(String key, Object value) {
        writeToCache(key, value, false, 0L, null);
    }

    @Override
    public void putWithExpiry(String key, Object value, long timeout, TimeUnit unit) {
        writeToCache(key, value, true, timeout, unit);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    private Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private void writeToCache(String key, Object value, boolean withExpiry, Long timeout, TimeUnit unit) {
        if (withExpiry) {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }
}
