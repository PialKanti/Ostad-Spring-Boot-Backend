package com.example.ecommerce.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 120, message = "Category name cannot exceed 120 characters")
        String name) {
}
