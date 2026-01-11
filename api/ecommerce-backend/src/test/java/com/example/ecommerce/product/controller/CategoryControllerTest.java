package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.repository.CategoryRepository;
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
@DisplayName("Category Controller Integration Tests")
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    /* ------------------------------------------------------------------
     * CREATE
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("POST /categories → 200 OK when request is valid")
    @WithMockUser(authorities = "CREATE_CATEGORY")
    void shouldCreateCategory() throws Exception {
        // Arrange
        CategoryCreateRequest request =
                new CategoryCreateRequest("Electronics", "ELECT-001");

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.data.code").value("ELECT-001"));
    }

    @Test
    @DisplayName("POST /categories → 403 Forbidden when permission missing")
    @WithMockUser(authorities = "VIEW_CATEGORY")
    void shouldReturnForbiddenWhenCreatingWithoutPermission() throws Exception {
        CategoryCreateRequest request =
                new CategoryCreateRequest("Electronics", "ELECT-001");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /categories → 400 Bad Request when validation fails")
    @WithMockUser(authorities = "CREATE_CATEGORY")
    void shouldFailWhenCreateRequestInvalid() throws Exception {
        // invalid: blank name & code
        CategoryCreateRequest request =
                new CategoryCreateRequest("", "");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* ------------------------------------------------------------------
     * GET BY ID
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("GET /categories/{id} → 200 OK when category exists")
    @WithMockUser(authorities = "VIEW_CATEGORY")
    void shouldGetCategoryById() throws Exception {
        // Arrange
        Category category = persistCategory("Electronics");

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(category.getId()))
                .andExpect(jsonPath("$.data.name").value("Electronics"));
    }

    @Test
    @DisplayName("GET /categories/{id} → 404 Not Found when category does not exist")
    @WithMockUser(authorities = "VIEW_CATEGORY")
    void shouldReturnNotFoundWhenCategoryMissing() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    /* ------------------------------------------------------------------
     * LIST
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("GET /categories → 200 OK with paginated result")
    @WithMockUser(authorities = "VIEW_CATEGORY")
    void shouldListCategories() throws Exception {
        // Arrange
        persistCategory("Electronics");
        persistCategory("Mobile");

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    /* ------------------------------------------------------------------
     * UPDATE
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("PUT /categories/{id} → 200 OK when authorized")
    @WithMockUser(authorities = "UPDATE_CATEGORY")
    void shouldUpdateCategory() throws Exception {
        // Arrange
        Category category = persistCategory("Electronics");
        CategoryUpdateRequest request =
                new CategoryUpdateRequest("Updated Electronics");

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Electronics"));
    }

    private Category persistCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setCode("CAT-" + UUID.randomUUID());
        category.setIsActive(true);
        return categoryRepository.saveAndFlush(category);
    }
}