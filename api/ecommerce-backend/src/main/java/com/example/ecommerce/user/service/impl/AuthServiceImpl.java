package com.example.ecommerce.user.service.impl;

import com.example.ecommerce.common.config.JwtProperties;
import com.example.ecommerce.common.exception.ResourceConflictException;
import com.example.ecommerce.common.service.JwtService;
import com.example.ecommerce.user.dto.request.LoginRequest;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.LoginResponse;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.entity.UserProfile;
import com.example.ecommerce.user.mapper.UserMapper;
import com.example.ecommerce.user.mapper.UserProfileMapper;
import com.example.ecommerce.user.repository.UserProfileRepository;
import com.example.ecommerce.user.repository.UserRepository;
import com.example.ecommerce.user.service.AuthService;
import com.example.ecommerce.user.service.BlackListedTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final BlackListedTokenService blackListedTokenService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final JwtProperties jwtProperties;

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
        User savedUser = userRepository.save(user);

        UserProfile profile = userProfileMapper.toEntity(request.profile(), savedUser);
        userProfileRepository.save(profile);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(user);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(Duration.ofMillis(jwtProperties.getExpirationMs()));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    public void logout(String authorizationHeader) {
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        Date expirationDate = jwtService.extractExpiration(token);

        blackListedTokenService.markAsBlacklisted(token, expirationDate);
    }
}
