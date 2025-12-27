package com.example.ecommerce.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LoginResponse(@JsonProperty("access_token")
                            String accessToken,
                            @JsonProperty("expires_at")
                            LocalDateTime expiresAt) {
}
