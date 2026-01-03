package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.dto.response.TokenRefreshResponse;
import lombok.Builder;

import java.time.Duration;

@Builder
public record TokenRefreshResult(TokenRefreshResponse tokenRefreshResponse,
                                 String refreshToken,
                                 Duration refreshTokenDuration) {
}
