package com.example.ecommerce.order.repository;

import com.example.ecommerce.order.entity.OrderReservation;
import com.example.ecommerce.order.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderReservationRepository extends JpaRepository<OrderReservation, Long> {

    List<OrderReservation> findByOrderId(Long orderId);

    List<OrderReservation> findByOrderIdAndStatus(Long orderId, ReservationStatus status);
}
