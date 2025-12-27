package com.example.ecommerce.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username should be between 3 and 50 characters long")
        String username,
        @NotBlank(message = "Password cannot be blank")
        String password) {
}
