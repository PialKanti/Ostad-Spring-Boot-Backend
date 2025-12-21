package com.example.ecommerce.product.service;

import com.example.ecommerce.product.dto.request.InventoryUpdateRequest;

public interface InventoryService {
    void updateStock(Long productId, InventoryUpdateRequest request);
}
