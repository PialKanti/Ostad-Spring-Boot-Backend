package com.example.ecommerce.product.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.ApiResponse;
import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.CategoryAdmin.BASE_CATEGORY_ADMIN)
@RequiredArgsConstructor
@Tag(
        name = "Category Admin",
        description = "Administrative operations for managing product categories"
)
public class CategoryAdminController {
    private final CategoryService categoryService;

    @Operation(
            summary = "Create a new category",
            description = "Creates a new product category. Category code must be unique.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Category code already exists",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.create(request)));
    }

    // TODO: Implement endpoint to retrieve category details by ID

    // TODO: Implement paginated category listing with filters
    //      (active status, parent category)

    // TODO: Implement endpoint to update category information
    //      (name, description)

    // TODO: Implement endpoint to activate or deactivate a category
    //      (soft delete via isActive flag)

    // TODO: Implement endpoint to permanently delete a category
}
