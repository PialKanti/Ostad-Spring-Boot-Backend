package com.example.inventory.mapper;

import com.example.inventory.dto.response.ReservationResponseDto;
import com.example.inventory.entity.StockReservation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    ReservationResponseDto toDto(StockReservation reservation);
}
