package com.example.ecommerce.cart.entity;

import com.example.ecommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
public class Cart extends BaseEntity {
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @PrePersist
    @PreUpdate
    private void onUpsert() {
        modifiedAt = LocalDateTime.now();
    }
}
