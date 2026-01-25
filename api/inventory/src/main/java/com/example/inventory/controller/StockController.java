package com.example.inventory.controller;

import com.example.inventory.dto.request.AdjustStockRequest;
import com.example.inventory.dto.request.DecreaseStockRequest;
import com.example.inventory.dto.request.IncreaseStockRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.StockResponseDto;
import com.example.inventory.service.StockService;
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
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Stock management operations")
public class StockController {

    private final StockService stockService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get stock by product ID", description = "Retrieves stock information for a specific product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Stock retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Stock not found for the given product ID")
    })
    public ResponseEntity<ApiResponse<StockResponseDto>> getStock(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStock(productId));
    }

    @PostMapping("/increase")
    @Operation(summary = "Increase stock", description = "Increases available stock quantity for a product. Creates stock record if it doesn't exist.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Stock increased successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data")
    })
    public ResponseEntity<ApiResponse<StockResponseDto>> increaseStock(
            @Valid @RequestBody IncreaseStockRequest request) {
        return ResponseEntity.ok(stockService.increaseStock(request));
    }

    @PostMapping("/decrease")
    @Operation(summary = "Decrease stock", description = "Decreases available stock quantity for a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Stock decreased successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Insufficient stock or invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Stock not found for the given product ID")
    })
    public ResponseEntity<ApiResponse<StockResponseDto>> decreaseStock(
            @Valid @RequestBody DecreaseStockRequest request) {
        return ResponseEntity.ok(stockService.decreaseStock(request));
    }

    @PutMapping("/adjust")
    @Operation(summary = "Adjust stock", description = "Sets the exact stock quantity for a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Stock adjusted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Stock not found for the given product ID")
    })
    public ResponseEntity<ApiResponse<StockResponseDto>> adjustStock(
            @Valid @RequestBody AdjustStockRequest request) {
        return ResponseEntity.ok(stockService.adjustStock(request));
    }
}
