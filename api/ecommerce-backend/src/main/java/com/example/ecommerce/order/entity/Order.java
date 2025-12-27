package com.example.ecommerce.order.entity;

import com.example.ecommerce.common.entity.BaseEntity;
import com.example.ecommerce.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "subtotal", nullable = false)
    private Double subTotal;

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;

    @Column(name = "delivery_charge", nullable = false)
    private Double deliveryCharge;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
