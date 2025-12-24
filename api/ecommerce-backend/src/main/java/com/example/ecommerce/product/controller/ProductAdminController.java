package com.example.ecommerce.product.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.ApiResponse;
import com.example.ecommerce.product.dto.request.InventoryUpdateRequest;
import com.example.ecommerce.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.product.entity.Product;
import com.example.ecommerce.product.service.InventoryService;
import com.example.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.ProductAdmin.BASE_PRODUCT_ADMIN)
@RequiredArgsConstructor
@Tag(
        name = "Product Admin",
        description = "Administrative operations for managing products")
public class ProductAdminController {
    private final ProductService productService;
    private final InventoryService inventoryService;

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product in the system. Validates SKU uniqueness and category existence.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Product created successfully",
                            content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Validation error or bad request",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Duplicate SKU or resource conflict",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.create(request)));
    }

    @PutMapping(ApiEndpoints.ProductAdmin.PRODUCT_INVENTORY)
    public ResponseEntity<ApiResponse<Void>> updateStock(@PathVariable Long productId, @Valid @RequestBody InventoryUpdateRequest request) {
        inventoryService.updateStock(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully."));
    }
}
