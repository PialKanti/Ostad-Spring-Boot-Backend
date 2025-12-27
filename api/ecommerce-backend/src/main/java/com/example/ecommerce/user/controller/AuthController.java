package com.example.ecommerce.user.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.response.ApiResponse;
import com.example.ecommerce.user.dto.request.LoginRequest;
import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.LoginResponse;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;
import com.example.ecommerce.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Authentication",
        description = "Endpoints for user registration and login"
)
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with profile information.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = RegisteredUserResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Conflict - Username or email already exists",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PostMapping(ApiEndpoints.Auth.REGISTER)
    public ResponseEntity<ApiResponse<RegisteredUserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        RegisteredUserResponse user = authService.registerUser(request);

        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns a JWT access token.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PostMapping(ApiEndpoints.Auth.LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User logged in successfully", authService.login(request)));
    }
}
