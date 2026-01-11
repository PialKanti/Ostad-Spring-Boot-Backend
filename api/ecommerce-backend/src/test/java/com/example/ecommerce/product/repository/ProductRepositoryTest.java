package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    /* ------------------------------------------------------------------
     * Save
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProduct() {
        // Arrange
        Category category = persistCategory("Electronics");
        Product product = newProduct("Smartphone", "PROD-001", 500.0, category);

        // Act
        Product saved = productRepository.save(product);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSku()).isEqualTo(product.getSku());
        assertThat(saved.getName()).isEqualTo("Smartphone");
        assertThat(saved.getCategory()).isEqualTo(category);
        assertThat(saved.getIsActive()).isTrue();
    }

    /* ------------------------------------------------------------------
     * Exists
     * ------------------------------------------------------------------ */

    @Test
    @DisplayName("Should return true when SKU exists")
    void shouldReturnTrueWhenSkuExists() {
        // Arrange
        Category category = persistCategory("Electronics");
        Product product = newProduct("Laptop", "PROD-002", 1000.0, category);
        entityManager.persistAndFlush(product);

        // Act
        boolean exists = productRepository.existsBySku(product.getSku());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when SKU does not exist")
    void shouldReturnFalseWhenSkuDoesNotExist() {
        // Arrange
        String nonExistingSku = "NON-EXISTENT";

        // Act
        boolean exists = productRepository.existsBySku(nonExistingSku);

        // Assert
        assertThat(exists).isFalse();
    }

    /* ------------------------------------------------------------------
     * Test Helpers
     * ------------------------------------------------------------------ */

    private Category persistCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setCode(uniqueCategoryCode());
        category.setIsActive(true);
        entityManager.persistAndFlush(category);
        return category;
    }

    private Product newProduct(
            String name,
            String sku,
            double price,
            Category category
    ) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setPrice(price);
        product.setCategory(category);
        product.setIsActive(true);
        return product;
    }

    private String uniqueCategoryCode() {
        return "CAT-" + UUID.randomUUID();
    }
}
