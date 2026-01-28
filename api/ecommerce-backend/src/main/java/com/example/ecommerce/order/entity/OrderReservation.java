package com.example.ecommerce.order.entity;

import com.example.ecommerce.common.entity.BaseEntity;
import com.example.ecommerce.order.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;
}
