package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save a product")
    void shouldSaveProduct() {
        Category category = new Category();
        category.setName("Electronics");
        category.setCode("ELECT-001");
        category.setIsActive(true);
        entityManager.persist(category);

        Product product = new Product();
        product.setSku("PROD-001");
        product.setName("Smartphone");
        product.setPrice(500.0);
        product.setCategory(category);
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
        assertThat(savedProduct.getSku()).isEqualTo("PROD-001");
        assertThat(savedProduct.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("Should return true when sku exists")
    void shouldReturnTrueWhenSkuExists() {
        Category category = new Category();
        category.setName("Electronics");
        category.setCode("ELECT-001");
        category.setIsActive(true);
        entityManager.persist(category);

        Product product = new Product();
        product.setSku("PROD-002");
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setCategory(category);
        product.setIsActive(true);
        entityManager.persist(product);
        entityManager.flush();

        boolean exists = productRepository.existsBySku("PROD-002");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when sku does not exist")
    void shouldReturnFalseWhenSkuDoesNotExist() {
        boolean exists = productRepository.existsBySku("NON-EXISTENT");

        assertThat(exists).isFalse();
    }
}
