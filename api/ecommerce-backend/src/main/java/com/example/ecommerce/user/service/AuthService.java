package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;

public interface AuthService {
    RegisteredUserResponse registerUser(UserRegistrationRequest request);
}
