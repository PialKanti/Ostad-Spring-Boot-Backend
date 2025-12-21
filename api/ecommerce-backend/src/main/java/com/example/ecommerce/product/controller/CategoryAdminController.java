package com.example.ecommerce.product.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.ApiResponse;
import com.example.ecommerce.common.dto.PaginatedResponse;
import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid CategoryCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.create(request)));
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves details of a category by its unique identifier.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Category.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    @Operation(
            summary = "List categories",
            description = "Retrieves a paginated list of categories.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Categories listed successfully",
                            content = @Content(schema = @Schema(implementation = PaginatedResponse.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<Category>>> listCategories(@RequestParam(name = "page", defaultValue = "0")
                                                                                   Integer page,
                                                                                   @RequestParam(name = "size", defaultValue = "10")
                                                                                   Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(categoryService.getAll(pageable))));
    }

    @Operation(
            summary = "Update category details",
            description = "Updates the name and description of an existing category.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category updated successfully",
                            content = @Content(schema = @Schema(implementation = Category.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
                                                                @Valid @RequestBody CategoryUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(id, request)));
    }

    @Operation(
            summary = "Activate or deactivate category",
            description = "Toggles the active status of a category (soft delete via isActive flag).",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category status updated successfully",
                            content = @Content(schema = @Schema(implementation = Category.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content
                    )
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Category>> toggleCategoryStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.toggleStatus(id, isActive)));
    }

    @Operation(
            summary = "Delete category permanently",
            description = "Deletes a category permanently from the system.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "Category deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
