package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.LoginResult;
import com.example.ecommerce.user.dto.TokenRefreshResult;
import com.example.ecommerce.user.dto.request.LoginRequest;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;

public interface AuthService {
    RegisteredUserResponse registerUser(UserRegistrationRequest request);

    LoginResult login(LoginRequest request);

    void logout(String authorizationHeader);

    TokenRefreshResult refreshToken(String rawRefreshToken);
}
