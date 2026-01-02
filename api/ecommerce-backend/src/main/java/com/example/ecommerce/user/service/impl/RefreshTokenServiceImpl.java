package com.example.ecommerce.user.service.impl;

import com.example.ecommerce.common.config.RefreshTokenProperties;
import com.example.ecommerce.user.dto.RefreshTokenData;
import com.example.ecommerce.user.entity.RefreshToken;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.repository.RefreshTokenRepository;
import com.example.ecommerce.user.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProperties refreshTokenProperties;

    @Override
    public RefreshTokenData create(User user) {
        String rawToken = generateSecureToken();
        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(hashedToken)
                .expiryDate(
                        LocalDateTime.now()
                                .plusSeconds(refreshTokenProperties.getExpirationSeconds())
                )
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return RefreshTokenData.builder()
                .rawToken(rawToken)
                .expirySeconds(refreshTokenProperties.getExpirationSeconds())
                .build();
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64]; // 512 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}
