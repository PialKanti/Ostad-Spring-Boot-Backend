package com.example.inventory.service;

import com.example.inventory.dto.request.AdjustStockRequest;
import com.example.inventory.dto.request.DecreaseStockRequest;
import com.example.inventory.dto.request.IncreaseStockRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.StockResponseDto;

public interface StockService {

    ApiResponse<StockResponseDto> getStock(Long productId);

    ApiResponse<StockResponseDto> increaseStock(IncreaseStockRequest request);

    ApiResponse<StockResponseDto> decreaseStock(DecreaseStockRequest request);

    ApiResponse<StockResponseDto> adjustStock(AdjustStockRequest request);
}
