package com.example.ecommerce.product.service.impl;

import com.example.ecommerce.common.dto.request.StockReservationRequest;
import com.example.ecommerce.common.exception.OutOfStockException;
import com.example.ecommerce.product.dto.request.InventoryUpdateRequest;
import com.example.ecommerce.product.entity.Inventory;
import com.example.ecommerce.product.repository.InventoryRepository;
import com.example.ecommerce.product.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public void updateStock(Long productId, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product with ID: " + productId + "."));

        inventory.setTotalQuantity(inventory.getTotalQuantity() + request.quantity());
        inventoryRepository.save(inventory);
    }

    @Override
    public void checkAndReserveStock(List<StockReservationRequest> requests) {
        List<Long> productIds = requests.stream()
                .map(StockReservationRequest::productId)
                .toList();

        List<Inventory> inventories = inventoryRepository.findAllByIdIn(productIds);

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(inventory -> inventory.getProduct().getId(), inventory -> inventory));

        for (StockReservationRequest request : requests) {
            Inventory inventory = inventoryMap.get(request.productId());

            int available = inventory.getTotalQuantity() - inventory.getReservedQuantity();
            if (request.quantity() > available) {
                throw new OutOfStockException(
                        String.format("Insufficient stock for product (ID: %d). Requested: %d, Available: %d.",
                                request.productId(), request.quantity(), available));
            }

            inventory.setReservedQuantity(inventory.getReservedQuantity() + request.quantity());
        }

        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void finalizeReservedStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found for product " + productId));

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() - quantity);

        inventoryRepository.save(inventory);
    }
}
