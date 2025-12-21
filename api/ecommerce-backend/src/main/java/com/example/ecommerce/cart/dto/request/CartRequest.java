package com.example.ecommerce.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartRequest(@NotNull(message = "User ID must not be null")
                          Long userId,
                          @NotNull(message = "Quantity must not be null")
                          @Min(value = 0, message = "Quantity must be at least 0")
                          Integer quantity) {
}