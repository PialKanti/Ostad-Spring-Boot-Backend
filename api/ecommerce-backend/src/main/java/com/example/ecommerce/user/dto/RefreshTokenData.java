package com.example.ecommerce.user.dto;

import lombok.Builder;

@Builder
public record RefreshTokenData(String rawToken,
                               long expirySeconds) {
}
