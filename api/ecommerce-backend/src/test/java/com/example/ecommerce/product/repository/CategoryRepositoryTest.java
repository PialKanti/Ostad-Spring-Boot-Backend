package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save a category")
    void shouldSaveCategory() {
        Category category = new Category();
        category.setName("Electronics");
        category.setCode("ELECT-001");
        category.setIsActive(true);

        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isGreaterThan(0);
        assertThat(savedCategory.getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Should find category by code")
    void shouldFindCategoryByCode() {
        Category category = new Category();
        category.setName("Mobile");
        category.setCode("MOB-001");
        category.setIsActive(true);
        entityManager.persist(category);
        entityManager.flush();

        Optional<Category> found = categoryRepository.findByCode("MOB-001");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mobile");
    }

    @Test
    @DisplayName("Should return true when code exists")
    void shouldReturnTrueWhenCodeExists() {
        Category category = new Category();
        category.setName("Laptops");
        category.setCode("LAP-001");
        category.setIsActive(true);
        entityManager.persist(category);
        entityManager.flush();

        boolean exists = categoryRepository.existsByCode("LAP-001");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when code does not exist")
    void shouldReturnFalseWhenCodeDoesNotExist() {
        boolean exists = categoryRepository.existsByCode("NON-EXISTENT");

        assertThat(exists).isFalse();
    }
}
