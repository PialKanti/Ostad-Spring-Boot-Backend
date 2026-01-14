package com.example.ecommerce.product.service;

import com.example.ecommerce.common.dto.request.StockReservationRequest;
import com.example.ecommerce.product.dto.request.InventoryUpdateRequest;

import java.util.List;

/**
 * Service interface for inventory and stock management.
 *
 * <p>Provides methods to update stock quantities, reserve stock
 * during checkout, and finalize reservations after payment.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface InventoryService {

    /**
     * Updates product stock quantity by adding the specified amount.
     *
     * @param productId the product ID
     * @param request contains the quantity to add (can be negative for reduction)
     * @throws jakarta.persistence.EntityNotFoundException if inventory not found
     */
    void updateStock(Long productId, InventoryUpdateRequest request);

    /**
     * Validates stock availability and reserves quantities for multiple products.
     *
     * @param requests list of product IDs and quantities to reserve
     * @throws com.example.ecommerce.common.exception.OutOfStockException if insufficient stock
     */
    void checkAndReserveStock(List<StockReservationRequest> requests);

    /**
     * Finalizes reserved stock after successful payment by reducing both reserved and total quantities.
     *
     * @param productId the product ID
     * @param quantity the quantity to finalize
     * @throws IllegalStateException if inventory not found
     */
    void finalizeReservedStock(Long productId, int quantity);
}
