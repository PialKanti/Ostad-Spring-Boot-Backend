package com.example.inventory.service.impl;

import com.example.inventory.dto.request.CreateReservationRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.ReservationResponseDto;
import com.example.inventory.entity.InventoryTransaction;
import com.example.inventory.entity.Stock;
import com.example.inventory.entity.StockReservation;
import com.example.inventory.enums.ReservationStatus;
import com.example.inventory.enums.TransactionType;
import com.example.inventory.exception.InsufficientStockException;
import com.example.inventory.exception.ReservationNotFoundException;
import com.example.inventory.exception.StockNotFoundException;
import com.example.inventory.mapper.ReservationMapper;
import com.example.inventory.repository.InventoryTransactionRepository;
import com.example.inventory.repository.StockRepository;
import com.example.inventory.repository.StockReservationRepository;
import com.example.inventory.service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing stock reservations.
 *
 * <p>This service handles the reservation lifecycle:</p>
 * <ul>
 *   <li><b>Create</b>: Reserve stock for a pending order (stock moves from available to reserved)</li>
 *   <li><b>Confirm</b>: Finalize the sale (reserved stock is removed as it's now sold)</li>
 *   <li><b>Cancel/Expire</b>: Release the hold (reserved stock returns to available)</li>
 * </ul>
 *
 * <p><b>Stock Field Updates Summary:</b></p>
 * <pre>
 * ┌─────────────────┬─────────────────────────┬─────────────────────────┐
 * │ Operation       │ quantityAvailable       │ quantityReserved        │
 * ├─────────────────┼─────────────────────────┼─────────────────────────┤
 * │ CREATE          │ DECREASES (stock held)  │ INCREASES (stock held)  │
 * │ CONFIRM         │ No change               │ DECREASES (stock sold)  │
 * │ CANCEL/EXPIRE   │ INCREASES (stock back)  │ DECREASES (hold removed)│
 * └─────────────────┴─────────────────────────┴─────────────────────────┘
 * </pre>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private static final int RESERVATION_EXPIRY_MINUTES = 15;

    private final StockRepository stockRepository;
    private final StockReservationRepository reservationRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ReservationMapper reservationMapper;

    /**
     * Creates a new stock reservation for a pending order.
     *
     * <p><b>Stock Changes:</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} DECREASES by the requested quantity
     *       (stock is no longer available for other orders)</li>
     *   <li>{@code quantityReserved} INCREASES by the requested quantity
     *       (stock is now held for this order)</li>
     * </ul>
     *
     * @param request contains productId, quantity, and optional orderId
     * @return the created reservation with PENDING status
     * @throws StockNotFoundException if no stock record exists for the product
     * @throws InsufficientStockException if available stock is less than requested quantity
     */
    @Override
    @Transactional
    public ApiResponse<ReservationResponseDto> createReservation(CreateReservationRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new StockNotFoundException(request.productId()));

        if (stock.getQuantityAvailable() < request.quantity()) {
            throw new InsufficientStockException(request.productId(),
                    request.quantity(), stock.getQuantityAvailable());
        }

        int previousAvailable = stock.getQuantityAvailable();
        int previousReserved = stock.getQuantityReserved();

        // STOCK UPDATE: Move quantity from available to reserved
        // quantityAvailable DECREASES - this stock is no longer available for other orders
        // quantityReserved INCREASES - this stock is now held for this pending order
        int newAvailable = previousAvailable - request.quantity();
        int newReserved = previousReserved + request.quantity();

        stock.setQuantityAvailable(newAvailable);
        stock.setQuantityReserved(newReserved);
        stockRepository.save(stock);

        log.debug("CREATE RESERVATION: Product {} - quantityAvailable: {} -> {} (DECREASED by {}), " +
                        "quantityReserved: {} -> {} (INCREASED by {})",
                request.productId(), previousAvailable, newAvailable, request.quantity(),
                previousReserved, newReserved, request.quantity());

        StockReservation reservation = StockReservation.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .status(ReservationStatus.PENDING)
                .orderId(request.orderId())
                .expiryTime(LocalDateTime.now().plusMinutes(RESERVATION_EXPIRY_MINUTES))
                .build();
        reservation = reservationRepository.save(reservation);

        logTransaction(request.productId(), TransactionType.RESERVE,
                request.quantity(), previousAvailable, newAvailable, reservation.getId());

        return ApiResponse.success("Reservation created successfully", reservationMapper.toDto(reservation));
    }

    /**
     * Confirms a pending reservation when an order is completed.
     *
     * <p><b>Stock Changes:</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} remains UNCHANGED
     *       (it was already reduced when the reservation was created)</li>
     *   <li>{@code quantityReserved} DECREASES by the reserved quantity
     *       (the hold is released because the stock is now sold/shipped)</li>
     * </ul>
     *
     * <p>After confirmation, the reserved quantity is effectively "sold" and removed
     * from inventory tracking entirely.</p>
     *
     * @param reservationId ID of the reservation to confirm
     * @return the confirmed reservation with CONFIRMED status
     * @throws ReservationNotFoundException if the reservation doesn't exist
     */
    @Override
    @Transactional
    public ApiResponse<ReservationResponseDto> confirmReservation(Long reservationId) {
        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            return ApiResponse.error("Reservation is not in PENDING status");
        }

        Long productId = reservation.getProductId();
        Integer quantity = reservation.getQuantity();

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException(productId));

        int previousReserved = stock.getQuantityReserved();

        // STOCK UPDATE: Remove from reserved (stock is now sold)
        // quantityAvailable stays UNCHANGED - it was already reduced when reservation was created
        // quantityReserved DECREASES - the hold is released because this stock is now sold
        int newReserved = previousReserved - quantity;

        stock.setQuantityReserved(newReserved);
        stockRepository.save(stock);

        log.debug("CONFIRM RESERVATION: Product {} - quantityAvailable: {} (UNCHANGED), " +
                        "quantityReserved: {} -> {} (DECREASED by {}, stock is now SOLD)",
                productId, stock.getQuantityAvailable(), previousReserved, newReserved, quantity);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        StockReservation savedReservation = reservationRepository.save(reservation);

        logTransaction(productId, TransactionType.CONFIRM, quantity, previousReserved, newReserved, reservationId);

        return ApiResponse.success("Reservation confirmed successfully", reservationMapper.toDto(savedReservation));
    }

    /**
     * Cancels a pending reservation and returns stock to available inventory.
     *
     * <p><b>Stock Changes:</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} INCREASES by the reserved quantity
     *       (stock is returned and can be reserved by other orders)</li>
     *   <li>{@code quantityReserved} DECREASES by the reserved quantity
     *       (the hold is released)</li>
     * </ul>
     *
     * <p>This operation reverses the stock changes made during reservation creation,
     * effectively returning the stock to available inventory.</p>
     *
     * @param reservationId ID of the reservation to cancel
     * @return the cancelled reservation with CANCELLED status
     * @throws ReservationNotFoundException if the reservation doesn't exist
     */
    @Override
    @Transactional
    public ApiResponse<ReservationResponseDto> cancelReservation(Long reservationId) {
        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            return ApiResponse.error("Reservation is not in PENDING status");
        }

        Long productId = reservation.getProductId();
        Integer quantity = reservation.getQuantity();

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException(productId));

        int previousAvailable = stock.getQuantityAvailable();
        int previousReserved = stock.getQuantityReserved();

        // STOCK UPDATE: Return reserved stock back to available
        // quantityAvailable INCREASES - stock is returned and can be reserved by other orders
        // quantityReserved DECREASES - the hold is released
        int newAvailable = previousAvailable + quantity;
        int newReserved = previousReserved - quantity;

        stock.setQuantityAvailable(newAvailable);
        stock.setQuantityReserved(newReserved);
        stockRepository.save(stock);

        log.debug("CANCEL RESERVATION: Product {} - quantityAvailable: {} -> {} (INCREASED by {}, stock RETURNED), " +
                        "quantityReserved: {} -> {} (DECREASED by {})",
                productId, previousAvailable, newAvailable, quantity, previousReserved, newReserved, quantity);

        reservation.setStatus(ReservationStatus.CANCELLED);
        StockReservation savedReservation = reservationRepository.save(reservation);

        logTransaction(productId, TransactionType.CANCEL, quantity, previousAvailable, newAvailable, reservationId);

        return ApiResponse.success("Reservation cancelled successfully", reservationMapper.toDto(savedReservation));
    }

    /**
     * Expires all pending reservations that have passed their expiry time.
     *
     * <p>This method is called by the scheduled task to automatically release
     * stock from expired reservations. The stock changes are identical to
     * manual cancellation.</p>
     *
     * <p><b>Stock Changes (per expired reservation):</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} INCREASES by the reserved quantity
     *       (stock is returned to available inventory)</li>
     *   <li>{@code quantityReserved} DECREASES by the reserved quantity
     *       (the expired hold is released)</li>
     * </ul>
     *
     * @return list of all reservations that were expired and cancelled
     */
    @Override
    @Transactional
    public ApiResponse<List<ReservationResponseDto>> expireOldReservations() {
        List<StockReservation> expiredReservations = reservationRepository
                .findByStatusAndExpiryTimeBefore(ReservationStatus.PENDING, LocalDateTime.now());

        if (expiredReservations.isEmpty()) {
            log.debug("No expired reservations found");
            return ApiResponse.success("No expired reservations found", new ArrayList<>());
        }

        log.info("Found {} expired reservations to process", expiredReservations.size());

        List<ReservationResponseDto> cancelledReservations = new ArrayList<>();

        for (StockReservation reservation : expiredReservations) {
            Long productId = reservation.getProductId();
            Integer quantity = reservation.getQuantity();
            Long reservationId = reservation.getId();

            Stock stock = stockRepository.findByProductId(productId).orElse(null);
            if (stock != null) {
                int previousAvailable = stock.getQuantityAvailable();
                int previousReserved = stock.getQuantityReserved();

                // STOCK UPDATE: Return expired reserved stock back to available
                // quantityAvailable INCREASES - stock is returned and can be reserved by other orders
                // quantityReserved DECREASES - the expired hold is released
                int newAvailable = previousAvailable + quantity;
                int newReserved = previousReserved - quantity;

                stock.setQuantityAvailable(newAvailable);
                stock.setQuantityReserved(newReserved);
                stockRepository.save(stock);

                log.info("EXPIRE RESERVATION #{}: Product {} - quantityAvailable: {} -> {} (INCREASED by {}, stock RETURNED), " +
                                "quantityReserved: {} -> {} (DECREASED by {})",
                        reservationId, productId, previousAvailable, newAvailable, quantity,
                        previousReserved, newReserved, quantity);

                logTransaction(productId, TransactionType.CANCEL, quantity, previousAvailable, newAvailable, reservationId);
            } else {
                log.warn("Stock not found for product {} while expiring reservation #{}, skipping stock update",
                        productId, reservationId);
            }

            reservation.setStatus(ReservationStatus.CANCELLED);
            StockReservation savedReservation = reservationRepository.save(reservation);
            cancelledReservations.add(reservationMapper.toDto(savedReservation));
        }

        log.info("Successfully expired {} reservations", cancelledReservations.size());

        return ApiResponse.success(
                String.format("Expired %d reservations", cancelledReservations.size()),
                cancelledReservations);
    }

    /**
     * Logs an inventory transaction for audit purposes.
     *
     * @param productId ID of the product affected
     * @param type type of transaction (RESERVE, CONFIRM, CANCEL)
     * @param quantityChanged quantity that was changed
     * @param previousQty quantity before the change
     * @param newQty quantity after the change
     * @param referenceId ID of the related reservation
     */
    private void logTransaction(Long productId, TransactionType type,
                                Integer quantityChanged, Integer previousQty,
                                Integer newQty, Long referenceId) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .productId(productId)
                .transactionType(type)
                .quantityChanged(quantityChanged)
                .previousQty(previousQty)
                .newQty(newQty)
                .referenceId(referenceId)
                .build();
        transactionRepository.save(transaction);
    }
}
