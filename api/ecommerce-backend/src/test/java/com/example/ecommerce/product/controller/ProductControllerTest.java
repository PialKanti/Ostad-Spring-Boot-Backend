package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.dto.request.InventoryUpdateRequest;
import com.example.ecommerce.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.entity.Inventory;
import com.example.ecommerce.product.entity.Product;
import com.example.ecommerce.product.repository.CategoryRepository;
import com.example.ecommerce.product.repository.InventoryRepository;
import com.example.ecommerce.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Product Controller Integration Tests")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /* ------------------------------------------------------------------
     * CREATE PRODUCT
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("POST /products → 200 OK when request is valid")
    @WithMockUser(authorities = "CREATE_PRODUCT")
    void shouldCreateProduct() throws Exception {
        // Arrange
        Category category = persistCategory("Electronics");
        ProductCreateRequest request = new ProductCreateRequest(
                "SKU-001",
                "Laptop",
                "High performance laptop",
                1200.0,
                category.getCode(),
                "http://example.com/laptop.jpg"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value("Laptop"))
                .andExpect(jsonPath("$.data.sku").value("SKU-001"));
    }

    @Test
    @DisplayName("POST /products → 403 Forbidden when permission missing")
    @WithMockUser(authorities = "VIEW_PRODUCT")
    void shouldReturnForbiddenWhenCreatingWithoutPermission() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                "SKU-001",
                "Laptop",
                "High performance laptop",
                1200.0,
                "CAT-CODE",
                "http://example.com/laptop.jpg"
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /products → 400 Bad Request when validation fails")
    @WithMockUser(authorities = "CREATE_PRODUCT")
    void shouldFailWhenCreateRequestInvalid() throws Exception {
        // invalid: blank sku & name, null price & categoryCode
        ProductCreateRequest request = new ProductCreateRequest(
                "",
                "",
                "Desc",
                null,
                null,
                ""
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* ------------------------------------------------------------------
     * UPDATE STOCK
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("PUT /products/{id}/inventory → 200 OK when authorized")
    @WithMockUser(authorities = "UPDATE_INVENTORY")
    void shouldUpdateStock() throws Exception {
        // Arrange
        Category category = persistCategory("Electronics");
        Product product = persistProduct("Laptop", "SKU-UPDATE-STOCK", category);
        InventoryUpdateRequest request = new InventoryUpdateRequest(50);

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/{productId}/inventory", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock updated successfully."));
    }

    @Test
    @DisplayName("PUT /products/{id}/inventory → 403 Forbidden when permission missing")
    @WithMockUser(authorities = "CREATE_PRODUCT")
    void shouldReturnForbiddenWhenUpdatingStockWithoutPermission() throws Exception {
        InventoryUpdateRequest request = new InventoryUpdateRequest(50);

        mockMvc.perform(put("/api/v1/products/{productId}/inventory", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    /* ------------------------------------------------------------------
     * HELPERS
     * ------------------------------------------------------------------ */

    private Category persistCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setCode("CAT-" + UUID.randomUUID());
        category.setIsActive(true);
        return categoryRepository.saveAndFlush(category);
    }

    private Product persistProduct(String name, String sku, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setPrice(100.0);
        product.setCategory(category);
        product.setIsActive(true);
        Product savedProduct = productRepository.saveAndFlush(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setTotalQuantity(0);
        inventory.setReservedQuantity(0);
        inventoryRepository.saveAndFlush(inventory);

        return savedProduct;
    }
}
