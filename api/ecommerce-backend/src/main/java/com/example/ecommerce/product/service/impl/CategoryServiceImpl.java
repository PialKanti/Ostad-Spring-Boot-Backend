package com.example.ecommerce.product.service.impl;

import com.example.ecommerce.common.exception.ResourceConflictException;
import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.mapper.CategoryMapper;
import com.example.ecommerce.product.repository.CategoryRepository;
import com.example.ecommerce.product.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    @Override
    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category update(Long id, CategoryUpdateRequest request) {
        Category category = getById(id);
        category.setName(request.name());
        return categoryRepository.save(category);
    }

    @Override
    public Category toggleStatus(Long id, Boolean isActive) {
        Category category = getById(id);
        category.setIsActive(isActive);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
