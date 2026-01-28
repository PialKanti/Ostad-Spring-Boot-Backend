package com.example.ecommerce.inventory.client;

import com.example.ecommerce.inventory.dto.ReservationResponse;
import com.example.ecommerce.inventory.dto.StockResponse;

public interface InventoryClient {

    StockResponse getStock(Long productId);

    StockResponse increaseStock(Long productId, Integer quantity);

    StockResponse decreaseStock(Long productId, Integer quantity);

    ReservationResponse createReservation(Long productId, Integer quantity, Long orderId);

    ReservationResponse confirmReservation(Long reservationId);

    ReservationResponse cancelReservation(Long reservationId);
}
