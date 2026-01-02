package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.RefreshTokenData;
import com.example.ecommerce.user.entity.User;

public interface RefreshTokenService {
    RefreshTokenData create(User user);
}
