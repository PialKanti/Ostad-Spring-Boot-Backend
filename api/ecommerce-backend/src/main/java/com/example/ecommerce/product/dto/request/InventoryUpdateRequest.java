package com.example.ecommerce.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryUpdateRequest(
        @NotNull
        @Min(value = 0, message = "Quantity must be greater than or equal to 0")
        int quantity
) {
}
