package com.example.ecommerce.user.service.impl;

import com.example.ecommerce.common.exception.ResourceConflictException;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.entity.UserProfile;
import com.example.ecommerce.user.mapper.UserMapper;
import com.example.ecommerce.user.mapper.UserProfileMapper;
import com.example.ecommerce.user.repository.UserProfileRepository;
import com.example.ecommerce.user.repository.UserRepository;
import com.example.ecommerce.user.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

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
}
