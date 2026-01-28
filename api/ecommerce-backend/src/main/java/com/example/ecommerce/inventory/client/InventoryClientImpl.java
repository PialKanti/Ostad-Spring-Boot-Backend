package com.example.ecommerce.inventory.client;

import com.example.ecommerce.common.exception.OutOfStockException;
import com.example.ecommerce.inventory.dto.CreateReservationRequest;
import com.example.ecommerce.inventory.dto.InventoryApiResponse;
import com.example.ecommerce.inventory.dto.ReservationResponse;
import com.example.ecommerce.inventory.dto.StockRequest;
import com.example.ecommerce.inventory.dto.StockResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClientImpl implements InventoryClient {

    private final RestClient inventoryRestClient;

    @Override
    public StockResponse getStock(Long productId) {
        log.debug("Getting stock for product {}", productId);

        InventoryApiResponse<StockResponse> response = inventoryRestClient.get()
                .uri("/api/stock/{productId}", productId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 404) {
                        throw new EntityNotFoundException("Stock not found for product " + productId);
                    }
                    throw new RuntimeException("Error getting stock: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response.getData() : null;
    }

    @Override
    public StockResponse increaseStock(Long productId, Integer quantity) {
        log.debug("Increasing stock for product {} by {}", productId, quantity);

        StockRequest request = new StockRequest(productId, quantity);

        InventoryApiResponse<StockResponse> response = inventoryRestClient.post()
                .uri("/api/stock/increase")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new RuntimeException("Error increasing stock: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response.getData() : null;
    }

    @Override
    public StockResponse decreaseStock(Long productId, Integer quantity) {
        log.debug("Decreasing stock for product {} by {}", productId, quantity);

        StockRequest request = new StockRequest(productId, quantity);

        InventoryApiResponse<StockResponse> response = inventoryRestClient.post()
                .uri("/api/stock/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 400) {
                        throw new OutOfStockException("Insufficient stock for product " + productId);
                    }
                    if (res.getStatusCode().value() == 404) {
                        throw new EntityNotFoundException("Stock not found for product " + productId);
                    }
                    throw new RuntimeException("Error decreasing stock: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response.getData() : null;
    }

    @Override
    public ReservationResponse createReservation(Long productId, Integer quantity, Long orderId) {
        log.debug("Creating reservation for product {} quantity {} order {}", productId, quantity, orderId);

        CreateReservationRequest request = new CreateReservationRequest(productId, quantity, orderId);

        InventoryApiResponse<ReservationResponse> response = inventoryRestClient.post()
                .uri("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 400) {
                        throw new OutOfStockException("Insufficient stock for product " + productId);
                    }
                    if (res.getStatusCode().value() == 404) {
                        throw new EntityNotFoundException("Stock not found for product " + productId);
                    }
                    throw new RuntimeException("Error creating reservation: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response.getData() : null;
    }

    @Override
    public ReservationResponse confirmReservation(Long reservationId) {
        log.debug("Confirming reservation {}", reservationId);

        InventoryApiResponse<ReservationResponse> response = inventoryRestClient.post()
                .uri("/api/reservations/{id}/confirm", reservationId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 404) {
                        throw new EntityNotFoundException("Reservation not found: " + reservationId);
                    }
                    if (res.getStatusCode().value() == 400) {
                        throw new IllegalStateException("Reservation " + reservationId + " is not in PENDING status");
                    }
                    throw new RuntimeException("Error confirming reservation: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response.getData() : null;
    }

    @Override
    public ReservationResponse cancelReservation(Long reservationId) {
        log.debug("Cancelling reservation {}", reservationId);

        InventoryApiResponse<ReservationResponse> response = inventoryRestClient.post()
                .uri("/api/reservations/{id}/cancel", reservationId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 404) {
                        throw new EntityNotFoundException("Reservation not found: " + reservationId);
                    }
                    if (res.getStatusCode().value() == 400) {
                        throw new IllegalStateException("Reservation " + reservationId + " is not in PENDING status");
                    }
                    throw new RuntimeException("Error cancelling reservation: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response.getData() : null;
    }
}
