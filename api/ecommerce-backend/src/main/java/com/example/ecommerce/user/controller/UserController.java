package com.example.ecommerce.user.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.response.ApiResponse;
import com.example.ecommerce.user.dto.request.ProfileUpdateRequest;
import com.example.ecommerce.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.User.BASE_USER)
@RequiredArgsConstructor
@Tag(name = "User", description = "Operations for managing user profiles")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update user profile", description = "Updates the profile of the currently logged-in user.")
    @PutMapping(ApiEndpoints.User.PROFILE)
    @PreAuthorize("hasAuthority(T(com.example.ecommerce.user.enums.PermissionType).UPDATE_PROFILE.name()) and #username == authentication.name")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(username, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", null));
    }
}
