package com.example.inventory.scheduler;

import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.ReservationResponseDto;
import com.example.inventory.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Scheduled task for automatically expiring stock reservations.
 *
 * <p>This scheduler runs every 5 minutes to check for expired reservations
 * and automatically reverts the stock to available inventory.</p>
 *
 * <p><b>What happens when a reservation expires:</b></p>
 * <ol>
 *   <li>The scheduler finds all PENDING reservations with expiry time in the past</li>
 *   <li>For each expired reservation:
 *     <ul>
 *       <li>{@code Stock.quantityAvailable} INCREASES (stock returned to available)</li>
 *       <li>{@code Stock.quantityReserved} DECREASES (hold is released)</li>
 *       <li>Reservation status changes from PENDING to CANCELLED</li>
 *       <li>A CANCEL transaction is logged for audit trail</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><b>Schedule:</b> Runs every 5 minutes (configurable via cron expression)</p>
 *
 * <p><b>Why automatic expiry is important:</b></p>
 * <ul>
 *   <li>Prevents stock from being indefinitely locked by abandoned carts</li>
 *   <li>Ensures fair availability for other customers</li>
 *   <li>Maintains accurate inventory counts</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryScheduler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReservationService reservationService;

    /**
     * Scheduled task that runs every 5 minutes to expire old reservations.
     *
     * <p>The cron expression "0 0/5 * * * ?" means:</p>
     * <ul>
     *   <li>0 - at second 0</li>
     *   <li>0/5 - every 5 minutes starting at minute 0 (0, 5, 10, 15, ...)</li>
     *   <li>* - every hour</li>
     *   <li>* - every day of month</li>
     *   <li>* - every month</li>
     *   <li>? - any day of week</li>
     * </ul>
     *
     * <p><b>Stock Revert Process:</b></p>
     * <pre>
     * For each expired reservation:
     *   1. Find Stock record for the product
     *   2. Stock.quantityAvailable += reservation.quantity  (INCREASE - stock returned)
     *   3. Stock.quantityReserved -= reservation.quantity   (DECREASE - hold released)
     *   4. Reservation.status = CANCELLED
     *   5. Log CANCEL transaction for audit
     * </pre>
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void expireReservations() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        log.info("=== RESERVATION EXPIRY SCHEDULER STARTED at {} ===", timestamp);

        try {
            // Call the service method to expire old reservations
            // This will:
            // 1. Find all PENDING reservations where expiryTime < now
            // 2. For each expired reservation:
            //    - INCREASE quantityAvailable (return stock to available pool)
            //    - DECREASE quantityReserved (release the hold)
            //    - Mark reservation as CANCELLED
            //    - Log the transaction for audit
            ApiResponse<List<ReservationResponseDto>> response = reservationService.expireOldReservations();

            List<ReservationResponseDto> expiredReservations = response.getData();

            if (expiredReservations != null && !expiredReservations.isEmpty()) {
                log.info("Successfully expired {} reservations. Stock has been returned to available inventory.",
                        expiredReservations.size());

                // Log details of each expired reservation for transparency
                for (ReservationResponseDto reservation : expiredReservations) {
                    log.info("  - Reservation #{} for Product {}: {} units returned to available stock",
                            reservation.getId(),
                            reservation.getProductId(),
                            reservation.getQuantity());
                }
            } else {
                log.debug("No expired reservations found during this run");
            }

        } catch (Exception e) {
            log.error("Error occurred while expiring reservations: {}", e.getMessage(), e);
        }

        log.info("=== RESERVATION EXPIRY SCHEDULER COMPLETED at {} ===",
                LocalDateTime.now().format(FORMATTER));
    }
}
