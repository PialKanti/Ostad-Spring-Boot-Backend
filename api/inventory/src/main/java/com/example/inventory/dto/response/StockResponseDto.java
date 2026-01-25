package com.example.inventory.dto.response;

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
public class StockResponseDto {

    private Long id;
    private Long productId;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private Integer reorderLevel;
    private LocalDateTime updatedAt;
}
