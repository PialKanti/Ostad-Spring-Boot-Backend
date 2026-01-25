package com.example.inventory.service;

import com.example.inventory.dto.request.CreateReservationRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.ReservationResponseDto;

import java.util.List;

public interface ReservationService {

    ApiResponse<ReservationResponseDto> createReservation(CreateReservationRequest request);

    ApiResponse<ReservationResponseDto> confirmReservation(Long reservationId);

    ApiResponse<ReservationResponseDto> cancelReservation(Long reservationId);

    ApiResponse<List<ReservationResponseDto>> expireOldReservations();
}
