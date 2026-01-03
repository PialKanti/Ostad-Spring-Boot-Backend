package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.dto.response.TokenRefreshResponse;
import lombok.Builder;

@Builder
public record RefreshTokenData(TokenRefreshResponse tokenRefreshResponse,
                               String rawToken,
                               long expirySeconds) {
}
