package com.example.ecommerce.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 100, message = "First name should be between 2 and 100 characters long")
        @JsonProperty("first_name")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters long")
        @JsonProperty("last_name")
        String lastName,

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username should be between 3 and 50 characters long")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(max = 255, message = "Password should not exceed 100 characters")
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Please provide a valid email address")
        @Size(max = 100, message = "Email should not exceed 100 characters")
        String email,

        @Valid
        ProfileCreateRequest profile
) {}
