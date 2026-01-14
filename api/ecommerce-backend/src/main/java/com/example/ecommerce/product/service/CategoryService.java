package com.example.ecommerce.product.service;

import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for product category management.
 *
 * <p>Provides CRUD operations and status management
 * for product categories.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request the category creation details
     * @return the newly created category
     * @throws com.example.ecommerce.common.exception.ResourceConflictException if category code exists
     */
    Category create(CategoryCreateRequest request);

    /**
     * Retrieves a category by its ID.
     *
     * @param id the category ID
     * @return the category entity
     * @throws jakarta.persistence.EntityNotFoundException if category not found
     */
    Category getById(Long id);

    /**
     * Retrieves all categories with pagination.
     *
     * @param pageable pagination parameters
     * @return a page of categories
     */
    Page<Category> getAll(Pageable pageable);

    /**
     * Updates an existing category's name.
     *
     * @param id the category ID to update
     * @param request the update details
     * @return the updated category
     * @throws jakarta.persistence.EntityNotFoundException if category not found
     */
    Category update(Long id, CategoryUpdateRequest request);

    /**
     * Toggles a category's active status.
     *
     * @param id the category ID
     * @param isActive the new active status
     * @return the updated category
     * @throws jakarta.persistence.EntityNotFoundException if category not found
     */
    Category toggleStatus(Long id, Boolean isActive);

    /**
     * Deletes a category by ID.
     *
     * @param id the category ID to delete
     * @throws jakarta.persistence.EntityNotFoundException if category not found
     */
    void delete(Long id);
}
