package com.example.ecommerce.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.security.refresh-token")
@Getter
@Setter
public class RefreshTokenProperties {
    private long expirationMs;

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}
