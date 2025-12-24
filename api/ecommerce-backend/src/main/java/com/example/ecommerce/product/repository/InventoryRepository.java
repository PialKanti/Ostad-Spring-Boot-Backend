package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    List<Inventory> findAllByIdIn(List<Long> ids);
}
