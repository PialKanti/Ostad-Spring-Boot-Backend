package com.example.ecommerce.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKey {
    BLACKLISTED_TOKEN("auth.blacklisted.%s");

    private final String key;

    public String format(Object... args) {
        return String.format(key, args);
    }
}
