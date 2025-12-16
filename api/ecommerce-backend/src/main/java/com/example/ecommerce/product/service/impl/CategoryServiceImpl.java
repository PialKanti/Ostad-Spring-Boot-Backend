package com.example.ecommerce.product.service.impl;

import com.example.ecommerce.common.exception.ResourceConflictException;
import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.mapper.CategoryMapper;
import com.example.ecommerce.product.repository.CategoryRepository;
import com.example.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category create(CategoryCreateRequest request) {
        if (categoryRepository.existsByCode(request.code())) {
            throw new ResourceConflictException("Category with Code '" + request.code() + "' already exists.");
        }

        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }
}
