package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.RefreshTokenData;
import com.example.ecommerce.user.entity.RefreshToken;
import com.example.ecommerce.user.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshTokenData create(User user);

    Optional<RefreshToken> findByToken(String token);

    RefreshTokenData rotate(RefreshToken oldToken);
}
