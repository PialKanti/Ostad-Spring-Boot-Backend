package com.example.inventory.repository;

import com.example.inventory.entity.StockReservation;
import com.example.inventory.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByStatusAndExpiryTimeBefore(ReservationStatus status, LocalDateTime now);

    List<StockReservation> findAllByProductId(Long productId);
}
