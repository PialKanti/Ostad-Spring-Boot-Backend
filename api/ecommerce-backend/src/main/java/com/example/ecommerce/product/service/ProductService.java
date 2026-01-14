package com.example.ecommerce.product.service;

import com.example.ecommerce.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.product.entity.Product;

/**
 * Service interface for product management operations.
 *
 * <p>Provides methods to create products with category
 * association and inventory initialization.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface ProductService {

    /**
     * Creates a new product with associated inventory.
     *
     * @param request the product creation details including SKU and category
     * @return the newly created product entity
     * @throws com.example.ecommerce.common.exception.ResourceConflictException if SKU already exists
     * @throws jakarta.persistence.EntityNotFoundException if category code not found
     */
    Product create(ProductCreateRequest request);
}
