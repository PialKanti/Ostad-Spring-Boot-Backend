package com.example.ecommerce.product.service.impl;

import com.example.ecommerce.product.dto.request.InventoryUpdateRequest;
import com.example.ecommerce.product.entity.Inventory;
import com.example.ecommerce.product.repository.InventoryRepository;
import com.example.ecommerce.product.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
