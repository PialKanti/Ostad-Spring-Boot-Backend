package com.example.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the stock/inventory for a product.
 *
 * <p>Stock quantities are managed through two key fields:</p>
 * <ul>
 *   <li><b>quantityAvailable</b>: Stock that can be reserved for new orders.
 *       This DECREASES when a reservation is created and INCREASES when a reservation
 *       is cancelled or expires.</li>
 *   <li><b>quantityReserved</b>: Stock that is temporarily held for pending orders.
 *       This INCREASES when a reservation is created and DECREASES when a reservation
 *       is confirmed, cancelled, or expires.</li>
 * </ul>
 *
 * <p><b>Stock Flow:</b></p>
 * <pre>
 * CREATE RESERVATION:  quantityAvailable -= qty, quantityReserved += qty
 * CONFIRM RESERVATION: quantityReserved -= qty (stock is sold, no longer tracked)
 * CANCEL/EXPIRE:       quantityAvailable += qty, quantityReserved -= qty (stock returned)
 * </pre>
 *
 * <p>Uses optimistic locking via {@code @Version} to prevent concurrent modification issues.</p>
 */
@Entity
@Table(name = "stock")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    /**
     * Quantity available for new reservations.
     * DECREASES when stock is reserved, INCREASES when reservation is cancelled/expired.
     */
    @Builder.Default
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable = 0;

    /**
     * Quantity currently held in pending reservations.
     * INCREASES when stock is reserved, DECREASES when reservation is confirmed/cancelled/expired.
     */
    @Builder.Default
    @Column(name = "quantity_reserved", nullable = false)
    private Integer quantityReserved = 0;

    @Builder.Default
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Version field for optimistic locking.
     * Prevents concurrent modifications from corrupting stock counts.
     */
    @Version
    private Long version;

    @PrePersist
    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
