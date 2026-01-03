package com.example.ecommerce.user.service.impl;

import com.example.ecommerce.common.config.JwtProperties;
import com.example.ecommerce.common.exception.InvalidRefreshTokenException;
import com.example.ecommerce.common.exception.ResourceConflictException;
import com.example.ecommerce.common.service.JwtService;
import com.example.ecommerce.user.dto.LoginResult;
import com.example.ecommerce.user.dto.RefreshTokenData;
import com.example.ecommerce.user.dto.TokenRefreshResult;
import com.example.ecommerce.user.dto.request.LoginRequest;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.LoginResponse;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;
import com.example.ecommerce.user.dto.response.TokenRefreshResponse;
import com.example.ecommerce.user.entity.RefreshToken;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.entity.UserProfile;
import com.example.ecommerce.user.mapper.UserMapper;
import com.example.ecommerce.user.mapper.UserProfileMapper;
import com.example.ecommerce.user.repository.UserProfileRepository;
import com.example.ecommerce.user.repository.UserRepository;
import com.example.ecommerce.user.service.AuthService;
import com.example.ecommerce.user.service.BlackListedTokenService;
import com.example.ecommerce.user.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import static com.example.ecommerce.common.constants.ApplicationConstant.BEARER_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final BlackListedTokenService blackListedTokenService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RegisteredUserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceConflictException("User with username '" + request.username() + "' already exists.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("User with email '" + request.email() + "' already exists.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        UserProfile profile = userProfileMapper.toEntity(request.profile(), savedUser);
        userProfileRepository.save(profile);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginResult login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(user);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(Duration.ofMillis(jwtProperties.getExpirationMs()));

        RefreshTokenData refreshTokenData = refreshTokenService.create(user);

        return LoginResult.builder()
                .loginResponse(LoginResponse.builder()
                        .accessToken(accessToken)
                        .expiresAt(expiresAt)
                        .build())
                .refreshToken(refreshTokenData.rawToken())
                .refreshTokenDuration(Duration.ofSeconds(refreshTokenData.expirySeconds()))
                .build();
    }

    @Override
    public void logout(String authorizationHeader) {
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        Date expirationDate = jwtService.extractExpiration(token);

        blackListedTokenService.markAsBlacklisted(token, expirationDate);
    }

    @Override
    public TokenRefreshResult refreshToken(String rawRefreshToken) {
        // Step 1: Validate refresh token
        RefreshToken refreshToken = refreshTokenService.findByToken(rawRefreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token"));

        if (Boolean.TRUE.equals(refreshToken.getIsRevoked()) || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token is revoked or expired");
        }

        // Step 2: Get user
        User user = refreshToken.getUser();

        // Step 3: Generate new access token
        String newAccessToken = jwtService.generateToken(user);
        LocalDateTime accessTokenExpiry = LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getExpirationMs()));

        // Step 4: Rotate refresh tokens
        RefreshTokenData newRefreshTokenData = refreshTokenService.rotate(refreshToken);

        // Step 5: Return result DTO
        return TokenRefreshResult.builder()
                .tokenRefreshResponse(TokenRefreshResponse.builder()
                        .accessToken(newAccessToken)
                        .expiresAt(accessTokenExpiry)
                        .build())
                .refreshToken(newRefreshTokenData.rawToken())
                .refreshTokenDuration(Duration.ofSeconds(newRefreshTokenData.expirySeconds()))
                .build();
    }
}
