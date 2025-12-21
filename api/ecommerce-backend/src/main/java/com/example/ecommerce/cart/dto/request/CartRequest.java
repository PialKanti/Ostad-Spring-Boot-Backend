package com.example.ecommerce.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartRequest(@NotNull(message = "User ID must not be null")
                          Long userId,
                          @NotEmpty(message = "Cart must contain at least one item")
                          List<CartItemRequest> items) {
    public static record CartItemRequest(@NotNull(message = "Product ID must not be null")
                                         Long productId,
                                         @NotNull(message = "Quantity must not be null")
                                         @Min(value = 1, message = "Quantity must be at least 1")
                                         Integer quantity) {
    }
}