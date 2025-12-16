package com.example.ecommerce.product.service;

import com.example.ecommerce.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.product.entity.Product;

public interface ProductService {
    Product create(ProductCreateRequest request);
}
