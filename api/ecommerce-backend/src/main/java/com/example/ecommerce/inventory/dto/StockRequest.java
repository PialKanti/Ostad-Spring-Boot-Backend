package com.example.ecommerce.inventory.dto;

public record StockRequest(
        Long productId,
        Integer quantity
) {
}
