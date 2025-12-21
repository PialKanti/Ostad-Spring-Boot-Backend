package com.example.ecommerce.product.service;

import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Category create(CategoryCreateRequest request);

    Category getById(Long id);

    Page<Category> getAll(Pageable pageable);

    Category update(Long id, CategoryUpdateRequest request);

    Category toggleStatus(Long id, Boolean isActive);

    void delete(Long id);
}
