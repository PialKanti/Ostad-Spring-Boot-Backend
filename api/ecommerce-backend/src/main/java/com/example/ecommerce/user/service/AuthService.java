package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.LoginResult;
import com.example.ecommerce.user.dto.TokenRefreshResult;
import com.example.ecommerce.user.dto.request.LoginRequest;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;

/**
 * Service interface for authentication and authorization operations.
 *
 * <p>Provides methods for user registration, login, logout,
 * and token refresh with JWT-based authentication.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface AuthService {

    /**
     * Registers a new user with CUSTOMER role and creates their profile.
     *
     * @param request user registration details including credentials and profile
     * @return the registered user response
     * @throws com.example.ecommerce.common.exception.ResourceConflictException if username or email exists
     * @throws jakarta.persistence.EntityNotFoundException if customer role not found
     */
    RegisteredUserResponse registerUser(UserRegistrationRequest request);

    /**
     * Authenticates user and generates access and refresh tokens.
     *
     * @param request login credentials
     * @return login result containing tokens and expiry information
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    LoginResult login(LoginRequest request);

    /**
     * Logs out user by blacklisting their access token.
     *
     * @param authorizationHeader the Bearer token from request header
     */
    void logout(String authorizationHeader);

    /**
     * Refreshes access token using a valid refresh token with rotation.
     *
     * @param rawRefreshToken the refresh token
     * @return new access and refresh tokens
     * @throws com.example.ecommerce.common.exception.InvalidRefreshTokenException if token invalid or expired
     */
    TokenRefreshResult refreshToken(String rawRefreshToken);
}
