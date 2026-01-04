package com.example.ecommerce.common.dto.response;

import lombok.Builder;

@Builder
public record ValidationErrorResponse(String field, String message) {
}
