package com.example.ecommerce.inventory.service;

import com.example.ecommerce.common.dto.request.StockReservationRequest;
import com.example.ecommerce.common.exception.OutOfStockException;
import com.example.ecommerce.inventory.client.InventoryClient;
import com.example.ecommerce.inventory.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryClientService {

    private final InventoryClient inventoryClient;

    public List<ReservationResponse> reserveStockForOrder(List<StockReservationRequest> requests, Long orderId) {
        List<ReservationResponse> createdReservations = new ArrayList<>();

        try {
            for (StockReservationRequest request : requests) {
                log.info("Creating reservation for product {} quantity {} order {}",
                        request.productId(), request.quantity(), orderId);

                ReservationResponse reservation = inventoryClient.createReservation(
                        request.productId(),
                        request.quantity(),
                        orderId
                );

                if (reservation != null) {
                    createdReservations.add(reservation);
                    log.info("Created reservation {} for product {}", reservation.getId(), request.productId());
                }
            }

            return createdReservations;

        } catch (OutOfStockException e) {
            log.warn("Stock reservation failed: {}. Rolling back {} reservations.", e.getMessage(), createdReservations.size());
            rollbackReservations(createdReservations);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during stock reservation. Rolling back {} reservations.", createdReservations.size(), e);
            rollbackReservations(createdReservations);
            throw new RuntimeException("Failed to reserve stock: " + e.getMessage(), e);
        }
    }

    public void confirmReservations(List<Long> reservationIds) {
        for (Long reservationId : reservationIds) {
            try {
                log.info("Confirming reservation {}", reservationId);
                inventoryClient.confirmReservation(reservationId);
                log.info("Confirmed reservation {}", reservationId);
            } catch (Exception e) {
                log.error("Failed to confirm reservation {}: {}", reservationId, e.getMessage());
                throw new RuntimeException("Failed to confirm reservation " + reservationId, e);
            }
        }
    }

    public void cancelReservations(List<Long> reservationIds) {
        for (Long reservationId : reservationIds) {
            try {
                log.info("Cancelling reservation {}", reservationId);
                inventoryClient.cancelReservation(reservationId);
                log.info("Cancelled reservation {}", reservationId);
            } catch (Exception e) {
                log.error("Failed to cancel reservation {}: {}", reservationId, e.getMessage());
            }
        }
    }

    private void rollbackReservations(List<ReservationResponse> reservations) {
        for (ReservationResponse reservation : reservations) {
            try {
                log.info("Rolling back reservation {}", reservation.getId());
                inventoryClient.cancelReservation(reservation.getId());
                log.info("Successfully rolled back reservation {}", reservation.getId());
            } catch (Exception e) {
                log.error("Failed to rollback reservation {}: {}", reservation.getId(), e.getMessage());
            }
        }
    }
}
