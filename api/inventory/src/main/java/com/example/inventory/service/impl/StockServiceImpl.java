package com.example.inventory.service.impl;

import com.example.inventory.dto.request.AdjustStockRequest;
import com.example.inventory.dto.request.DecreaseStockRequest;
import com.example.inventory.dto.request.IncreaseStockRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.StockResponseDto;
import com.example.inventory.entity.InventoryTransaction;
import com.example.inventory.entity.Stock;
import com.example.inventory.enums.TransactionType;
import com.example.inventory.exception.InsufficientStockException;
import com.example.inventory.exception.StockNotFoundException;
import com.example.inventory.mapper.StockMapper;
import com.example.inventory.repository.InventoryTransactionRepository;
import com.example.inventory.repository.StockRepository;
import com.example.inventory.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for direct stock management operations.
 *
 * <p>This service handles administrative stock operations that directly modify
 * the {@code quantityAvailable} field without going through the reservation system.</p>
 *
 * <p><b>Stock Field Updates Summary:</b></p>
 * <pre>
 * ┌─────────────────┬─────────────────────────┬─────────────────────────┐
 * │ Operation       │ quantityAvailable       │ quantityReserved        │
 * ├─────────────────┼─────────────────────────┼─────────────────────────┤
 * │ INCREASE        │ INCREASES (stock added) │ No change               │
 * │ DECREASE        │ DECREASES (stock removed)│ No change              │
 * │ ADJUST          │ SET to new value        │ No change               │
 * └─────────────────┴─────────────────────────┴─────────────────────────┘
 * </pre>
 *
 * <p><b>Note:</b> These operations only affect {@code quantityAvailable}.
 * The {@code quantityReserved} field is managed exclusively by the
 * {@link ReservationServiceImpl} through the reservation lifecycle.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final StockMapper stockMapper;

    /**
     * Retrieves the current stock information for a product.
     *
     * @param productId ID of the product
     * @return stock information including available and reserved quantities
     * @throws StockNotFoundException if no stock record exists for the product
     */
    @Override
    public ApiResponse<StockResponseDto> getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException(productId));
        return ApiResponse.success("Stock retrieved successfully", stockMapper.toDto(stock));
    }

    /**
     * Increases the available stock quantity (e.g., receiving new inventory).
     *
     * <p><b>Stock Changes:</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} INCREASES by the specified quantity
     *       (new stock is added to available inventory)</li>
     *   <li>{@code quantityReserved} remains UNCHANGED</li>
     * </ul>
     *
     * <p>If no stock record exists for the product, a new one is created.</p>
     *
     * @param request contains productId and quantity to add
     * @return updated stock information
     */
    @Override
    @Transactional
    public ApiResponse<StockResponseDto> increaseStock(IncreaseStockRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseGet(() -> Stock.builder()
                        .productId(request.productId())
                        .quantityAvailable(0)
                        .quantityReserved(0)
                        .reorderLevel(0)
                        .build());

        int previousQty = stock.getQuantityAvailable();

        // STOCK UPDATE: Add new inventory to available stock
        // quantityAvailable INCREASES - new stock is received and available for orders
        // quantityReserved stays UNCHANGED - this operation doesn't affect reservations
        int newQty = previousQty + request.quantity();

        stock.setQuantityAvailable(newQty);
        stock = stockRepository.save(stock);

        log.debug("INCREASE STOCK: Product {} - quantityAvailable: {} -> {} (INCREASED by {})",
                request.productId(), previousQty, newQty, request.quantity());

        logTransaction(request.productId(), TransactionType.STOCK_IN,
                request.quantity(), previousQty, newQty, null);

        return ApiResponse.success("Stock increased successfully", stockMapper.toDto(stock));
    }

    /**
     * Decreases the available stock quantity (e.g., manual adjustment, damage, loss).
     *
     * <p><b>Stock Changes:</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} DECREASES by the specified quantity
     *       (stock is removed from available inventory)</li>
     *   <li>{@code quantityReserved} remains UNCHANGED</li>
     * </ul>
     *
     * @param request contains productId and quantity to remove
     * @return updated stock information
     * @throws StockNotFoundException if no stock record exists for the product
     * @throws InsufficientStockException if available stock is less than quantity to decrease
     */
    @Override
    @Transactional
    public ApiResponse<StockResponseDto> decreaseStock(DecreaseStockRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new StockNotFoundException(request.productId()));

        int previousQty = stock.getQuantityAvailable();

        // STOCK UPDATE: Remove stock from available inventory
        // quantityAvailable DECREASES - stock is removed (damaged, lost, manual adjustment)
        // quantityReserved stays UNCHANGED - this operation doesn't affect reservations
        int newQty = previousQty - request.quantity();

        if (newQty < 0) {
            throw new InsufficientStockException(request.productId(), request.quantity(), previousQty);
        }

        stock.setQuantityAvailable(newQty);
        stock = stockRepository.save(stock);

        log.debug("DECREASE STOCK: Product {} - quantityAvailable: {} -> {} (DECREASED by {})",
                request.productId(), previousQty, newQty, request.quantity());

        logTransaction(request.productId(), TransactionType.STOCK_OUT,
                request.quantity(), previousQty, newQty, null);

        return ApiResponse.success("Stock decreased successfully", stockMapper.toDto(stock));
    }

    /**
     * Adjusts the available stock to an exact quantity (e.g., physical inventory count correction).
     *
     * <p><b>Stock Changes:</b></p>
     * <ul>
     *   <li>{@code quantityAvailable} is SET to the specified new quantity
     *       (may increase or decrease depending on current value)</li>
     *   <li>{@code quantityReserved} remains UNCHANGED</li>
     * </ul>
     *
     * @param request contains productId and the new exact quantity
     * @return updated stock information
     * @throws StockNotFoundException if no stock record exists for the product
     */
    @Override
    @Transactional
    public ApiResponse<StockResponseDto> adjustStock(AdjustStockRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new StockNotFoundException(request.productId()));

        int previousQty = stock.getQuantityAvailable();
        int newQty = request.newQuantity();
        int quantityChanged = newQty - previousQty;

        // STOCK UPDATE: Set available stock to exact quantity (inventory correction)
        // quantityAvailable is SET to the new value - may increase or decrease
        // quantityReserved stays UNCHANGED - this operation doesn't affect reservations
        stock.setQuantityAvailable(newQty);
        stock = stockRepository.save(stock);

        String changeDirection = quantityChanged >= 0 ? "INCREASED" : "DECREASED";
        log.debug("ADJUST STOCK: Product {} - quantityAvailable: {} -> {} ({} by {})",
                request.productId(), previousQty, newQty, changeDirection, Math.abs(quantityChanged));

        logTransaction(request.productId(), TransactionType.ADJUST,
                quantityChanged, previousQty, newQty, null);

        return ApiResponse.success("Stock adjusted successfully", stockMapper.toDto(stock));
    }

    /**
     * Logs an inventory transaction for audit purposes.
     *
     * @param productId ID of the product affected
     * @param type type of transaction (STOCK_IN, STOCK_OUT, ADJUST)
     * @param quantityChanged quantity that was changed (positive or negative)
     * @param previousQty quantity before the change
     * @param newQty quantity after the change
     * @param referenceId optional reference to related entity (null for direct stock operations)
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
