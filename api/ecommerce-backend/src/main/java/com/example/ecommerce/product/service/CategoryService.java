package com.example.ecommerce.product.service;

import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.entity.Category;

public interface CategoryService {
    Category create(CategoryCreateRequest request);
}
