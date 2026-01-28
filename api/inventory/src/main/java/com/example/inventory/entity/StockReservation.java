package com.example.inventory.entity;

import com.example.inventory.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a temporary stock reservation for a pending order.
 *
 * <p>Reservations hold stock for a limited time (default 15 minutes) while a customer
 * completes their purchase. If the order is not confirmed within the expiry time,
 * the reservation expires and the stock is automatically returned to available inventory.</p>
 *
 * <p><b>Reservation Lifecycle:</b></p>
 * <ul>
 *   <li><b>PENDING</b>: Initial state - stock is held, waiting for order confirmation</li>
 *   <li><b>CONFIRMED</b>: Order completed - stock is sold, reservation closed</li>
 *   <li><b>CANCELLED</b>: Manually cancelled or expired - stock returned to available</li>
 * </ul>
 *
 * <p><b>Note:</b> This entity does not use optimistic locking (@Version) because
 * the Stock entity handles concurrency control. Stock modifications are the critical
 * section that requires locking, not the reservation status updates.</p>
 */
@Entity
@Table(name = "stock_reservation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Quantity of stock reserved for this order.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Current status of the reservation (PENDING, CONFIRMED, or CANCELLED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    /**
     * Associated order ID (optional, may be null if reservation is created before order).
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * Time when this reservation expires and stock should be returned.
     * Only applicable when status is PENDING.
     */
    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
