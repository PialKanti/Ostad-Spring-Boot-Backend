package com.example.inventory.service.impl;

import com.example.inventory.dto.request.AdjustStockRequest;
import com.example.inventory.dto.request.DecreaseStockRequest;
import com.example.inventory.dto.request.IncreaseStockRequest;
import com.example.inventory.dto.response.ApiResponse;
import com.example.inventory.dto.response.StockResponseDto;
import com.example.inventory.entity.InventoryTransaction;
import com.example.inventory.entity.Stock;
import com.example.inventory.enums.TransactionType;
import com.example.inventory.exception.InsufficientStockException;
import com.example.inventory.exception.StockNotFoundException;
import com.example.inventory.mapper.StockMapper;
import com.example.inventory.repository.InventoryTransactionRepository;
import com.example.inventory.repository.StockRepository;
import com.example.inventory.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final StockMapper stockMapper;

    @Override
    public ApiResponse<StockResponseDto> getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException(productId));
        return ApiResponse.success("Stock retrieved successfully", stockMapper.toDto(stock));
    }

    @Override
    @Transactional
    public ApiResponse<StockResponseDto> increaseStock(IncreaseStockRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseGet(() -> Stock.builder()
                        .productId(request.productId())
                        .quantityAvailable(0)
                        .quantityReserved(0)
                        .reorderLevel(0)
                        .build());

        int previousQty = stock.getQuantityAvailable();
        int newQty = previousQty + request.quantity();

        stock.setQuantityAvailable(newQty);
        stock = stockRepository.save(stock);

        logTransaction(request.productId(), TransactionType.STOCK_IN,
                request.quantity(), previousQty, newQty, null);

        return ApiResponse.success("Stock increased successfully", stockMapper.toDto(stock));
    }

    @Override
    @Transactional
    public ApiResponse<StockResponseDto> decreaseStock(DecreaseStockRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new StockNotFoundException(request.productId()));

        int previousQty = stock.getQuantityAvailable();
        int newQty = previousQty - request.quantity();

        if (newQty < 0) {
            throw new InsufficientStockException(request.productId(), request.quantity(), previousQty);
        }

        stock.setQuantityAvailable(newQty);
        stock = stockRepository.save(stock);

        logTransaction(request.productId(), TransactionType.STOCK_OUT,
                request.quantity(), previousQty, newQty, null);

        return ApiResponse.success("Stock decreased successfully", stockMapper.toDto(stock));
    }

    @Override
    @Transactional
    public ApiResponse<StockResponseDto> adjustStock(AdjustStockRequest request) {
        Stock stock = stockRepository.findByProductId(request.productId())
                .orElseThrow(() -> new StockNotFoundException(request.productId()));

        int previousQty = stock.getQuantityAvailable();
        int newQty = request.newQuantity();
        int quantityChanged = newQty - previousQty;

        stock.setQuantityAvailable(newQty);
        stock = stockRepository.save(stock);

        logTransaction(request.productId(), TransactionType.ADJUST,
                quantityChanged, previousQty, newQty, null);

        return ApiResponse.success("Stock adjusted successfully", stockMapper.toDto(stock));
    }

    private void logTransaction(Long productId, TransactionType type,
                                Integer quantityChanged, Integer previousQty,
                                Integer newQty, Long referenceId) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .productId(productId)
                .transactionType(type)
                .quantityChanged(quantityChanged)
                .previousQty(previousQty)
                .newQty(newQty)
                .referenceId(referenceId)
                .build();
        transactionRepository.save(transaction);
    }
}
