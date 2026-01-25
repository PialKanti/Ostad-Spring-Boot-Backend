package com.example.inventory.controller;

import com.example.inventory.dto.request.CreateReservationRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.ReservationResponseDto;
import com.example.inventory.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Stock reservation management operations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create reservation", description = "Creates a new stock reservation for a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reservation created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Insufficient stock or invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Stock not found for the given product ID")
    })
    public ResponseEntity<ApiResponse<ReservationResponseDto>> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm reservation", description = "Confirms a pending reservation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reservation confirmed successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Reservation is not in PENDING status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found")
    })
    public ResponseEntity<ApiResponse<ReservationResponseDto>> confirmReservation(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel reservation", description = "Cancels a pending reservation and restores available stock")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reservation cancelled successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Reservation is not in PENDING status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found")
    })
    public ResponseEntity<ApiResponse<ReservationResponseDto>> cancelReservation(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }
}
