package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.request.ProfileUpdateRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void updateProfile(String username, ProfileUpdateRequest request);
}
