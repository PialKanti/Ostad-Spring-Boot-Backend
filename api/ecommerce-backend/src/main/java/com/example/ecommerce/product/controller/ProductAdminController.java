package com.example.ecommerce.product.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.ApiResponse;
import com.example.ecommerce.product.dto.response.ProductCreateRequest;
import com.example.ecommerce.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.ProductAdmin.BASE_PRODUCT_ADMIN)
@RequiredArgsConstructor
public class ProductAdminController {
    public ResponseEntity<ApiResponse<Product>> createProduct(ProductCreateRequest request){
        return ResponseEntity.ok(ApiResponse.success(new Product()));
    }
}
