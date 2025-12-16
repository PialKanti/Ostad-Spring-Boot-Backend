package com.example.ecommerce.product.service.impl;

import com.example.ecommerce.common.exception.ResourceConflictException;
import com.example.ecommerce.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.entity.Product;
import com.example.ecommerce.product.mapper.ProductMapper;
import com.example.ecommerce.product.repository.CategoryRepository;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Product create(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new ResourceConflictException("Product with SKU '" + request.sku() + "' already exists.");
        }

        Optional<Category> categoryOptional = categoryRepository.findByCode(request.categoryCode());
        if (categoryOptional.isEmpty()) {
            throw new EntityNotFoundException("Category with code '" + request.categoryCode() + "' not found.");
        }

        Product product = productMapper.toEntity(request, categoryOptional.get());
        return productRepository.save(product);
    }
}
