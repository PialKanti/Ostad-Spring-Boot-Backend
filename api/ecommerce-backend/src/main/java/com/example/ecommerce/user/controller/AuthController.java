package com.example.ecommerce.user.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.response.ApiResponse;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;
import com.example.ecommerce.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.Auth.BASE_AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiEndpoints.Auth.REGISTER)
    public ResponseEntity<ApiResponse<RegisteredUserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        RegisteredUserResponse user = authService.registerUser(request);

        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }
}
