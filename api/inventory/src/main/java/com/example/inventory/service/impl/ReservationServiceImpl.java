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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final int RESERVATION_EXPIRY_MINUTES = 15;

    private final StockRepository stockRepository;
    private final StockReservationRepository reservationRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ReservationMapper reservationMapper;

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
        int newAvailable = previousAvailable - request.quantity();

        stock.setQuantityAvailable(newAvailable);
        stock.setQuantityReserved(stock.getQuantityReserved() + request.quantity());
        stockRepository.save(stock);

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
        int newReserved = previousReserved - quantity;

        stock.setQuantityReserved(newReserved);
        stockRepository.save(stock);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        StockReservation savedReservation = reservationRepository.save(reservation);

        logTransaction(productId, TransactionType.CONFIRM, quantity, previousReserved, newReserved, reservationId);

        return ApiResponse.success("Reservation confirmed successfully", reservationMapper.toDto(savedReservation));
    }

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
        int newAvailable = previousAvailable + quantity;

        stock.setQuantityAvailable(newAvailable);
        stock.setQuantityReserved(stock.getQuantityReserved() - quantity);
        stockRepository.save(stock);

        reservation.setStatus(ReservationStatus.CANCELLED);
        StockReservation savedReservation = reservationRepository.save(reservation);

        logTransaction(productId, TransactionType.CANCEL, quantity, previousAvailable, newAvailable, reservationId);

        return ApiResponse.success("Reservation cancelled successfully", reservationMapper.toDto(savedReservation));
    }

    @Override
    @Transactional
    public ApiResponse<List<ReservationResponseDto>> expireOldReservations() {
        List<StockReservation> expiredReservations = reservationRepository
                .findByStatusAndExpiryTimeBefore(ReservationStatus.PENDING, LocalDateTime.now());

        List<ReservationResponseDto> cancelledReservations = new ArrayList<>();

        for (StockReservation reservation : expiredReservations) {
            Long productId = reservation.getProductId();
            Integer quantity = reservation.getQuantity();
            Long reservationId = reservation.getId();

            Stock stock = stockRepository.findByProductId(productId).orElse(null);
            if (stock != null) {
                int previousAvailable = stock.getQuantityAvailable();
                int newAvailable = previousAvailable + quantity;

                stock.setQuantityAvailable(newAvailable);
                stock.setQuantityReserved(stock.getQuantityReserved() - quantity);
                stockRepository.save(stock);

                logTransaction(productId, TransactionType.CANCEL, quantity, previousAvailable, newAvailable, reservationId);
            }

            reservation.setStatus(ReservationStatus.CANCELLED);
            StockReservation savedReservation = reservationRepository.save(reservation);
            cancelledReservations.add(reservationMapper.toDto(savedReservation));
        }

        return ApiResponse.success(
                String.format("Expired %d reservations", cancelledReservations.size()),
                cancelledReservations);
    }

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
