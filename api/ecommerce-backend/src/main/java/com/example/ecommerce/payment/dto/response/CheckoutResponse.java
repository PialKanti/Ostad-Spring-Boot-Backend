package com.example.ecommerce.payment.dto.response;

import lombok.Builder;

@Builder
public record CheckoutResponse(String checkoutUrl) {
}
