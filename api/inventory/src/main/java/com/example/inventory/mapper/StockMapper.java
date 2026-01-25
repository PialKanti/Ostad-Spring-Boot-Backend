package com.example.inventory.mapper;

import com.example.inventory.dto.response.StockResponseDto;
import com.example.inventory.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

    StockResponseDto toDto(Stock stock);
}
