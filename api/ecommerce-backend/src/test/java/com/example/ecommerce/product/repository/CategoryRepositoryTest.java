package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Category Repository Tests")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    /* ------------------------------------------------------------------
     * Save
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("Should save category successfully")
    void shouldSaveCategory() {
        // Arrange
        Category category = newCategory("Electronics", uniqueCode());

        // Act
        Category saved = categoryRepository.save(category);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Electronics");
        assertThat(saved.getCode()).isEqualTo(category.getCode());
        assertThat(saved.getIsActive()).isTrue();
    }

    /* ------------------------------------------------------------------
     * Find
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("Should find category by code")
    void shouldFindCategoryByCode() {
        // Arrange
        Category category = newCategory("Mobile", uniqueCode());
        categoryRepository.save(category);

        // Act
        Optional<Category> result =
                categoryRepository.findByCode(category.getCode());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(category.getId());
        assertThat(result.get().getName()).isEqualTo("Mobile");
    }

    /* ------------------------------------------------------------------
     * Exists
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("Should return true when category code exists")
    void shouldReturnTrueWhenCodeExists() {
        // Arrange
        Category category = newCategory("Laptop", uniqueCode());
        categoryRepository.save(category);

        // Act
        boolean exists =
                categoryRepository.existsByCode(category.getCode());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when category code does not exist")
    void shouldReturnFalseWhenCodeDoesNotExist() {
        // Arrange
        String nonExistingCode = "NON-EXISTENT";

        // Act
        boolean exists =
                categoryRepository.existsByCode(nonExistingCode);

        // Assert
        assertThat(exists).isFalse();
    }

    /* ------------------------------------------------------------------
     * Test Helpers
     * ------------------------------------------------------------------ */

    private Category newCategory(String name, String code) {
        Category category = new Category();
        category.setName(name);
        category.setCode(code);
        category.setIsActive(true);
        return category;
    }

    private String uniqueCode() {
        return "CAT-" + UUID.randomUUID();
    }
}