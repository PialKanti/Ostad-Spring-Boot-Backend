package com.example.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AdjustStockRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "New quantity is required")
        @PositiveOrZero(message = "New quantity must be zero or positive")
        Integer newQuantity
) {
}
