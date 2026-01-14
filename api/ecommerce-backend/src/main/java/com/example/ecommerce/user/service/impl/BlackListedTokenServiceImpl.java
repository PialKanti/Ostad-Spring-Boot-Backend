package com.example.ecommerce.user.service.impl;

import com.example.ecommerce.common.enums.RedisKey;
import com.example.ecommerce.common.service.CacheService;
import com.example.ecommerce.user.service.BlackListedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link BlackListedTokenService} using Redis cache.
 *
 * <p>Tokens are stored with TTL matching their original expiration
 * time for automatic cleanup.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class BlackListedTokenServiceImpl implements BlackListedTokenService {
    private final CacheService cacheService;

    @Override
    public void markAsBlacklisted(String token, Date expirationDate) {
        String redisKey = getRedisKey(token);
        long expiryDurationInMS = expirationDate.getTime() - System.currentTimeMillis();

        cacheService.putWithExpiry(redisKey, token, expiryDurationInMS, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String redisKey = getRedisKey(token);

        Optional<String> tokenOptional = cacheService.get(redisKey, String.class);
        return tokenOptional.isPresent();
    }

    private String getRedisKey(String token) {
        return RedisKey.BLACKLISTED_TOKEN.format(token);
    }
}
