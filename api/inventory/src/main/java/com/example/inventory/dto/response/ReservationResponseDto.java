package com.example.inventory.dto.response;

import com.example.inventory.enums.ReservationStatus;
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
public class ReservationResponseDto {

    private Long id;
    private Long productId;
    private Integer quantity;
    private ReservationStatus status;
    private Long orderId;
    private LocalDateTime expiryTime;
    private LocalDateTime createdAt;
}
