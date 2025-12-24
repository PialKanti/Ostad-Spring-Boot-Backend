package com.example.ecommerce.common.dto.request;

import lombok.Builder;

@Builder
public record StockReservationRequest(Long productId, int quantity) {
}
