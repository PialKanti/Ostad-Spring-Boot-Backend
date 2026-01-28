package com.example.ecommerce.inventory.dto;

public record CreateReservationRequest(
        Long productId,
        Integer quantity,
        Long orderId
) {
}
