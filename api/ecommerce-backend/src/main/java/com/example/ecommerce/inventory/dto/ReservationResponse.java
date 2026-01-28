package com.example.ecommerce.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private Long productId;
    private Integer quantity;
    private String status;
    private Long orderId;
    private LocalDateTime expiryTime;
    private LocalDateTime createdAt;
}
