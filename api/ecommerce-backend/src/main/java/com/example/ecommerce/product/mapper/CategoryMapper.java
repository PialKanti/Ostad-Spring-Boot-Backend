package com.example.ecommerce.product.mapper;

import com.example.ecommerce.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CategoryCreateRequest request);
}
