package com.example.ecommerce.product.mapper;

import com.example.ecommerce.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.product.entity.Category;
import com.example.ecommerce.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "category", source = "category")
    Product toEntity(ProductCreateRequest request, Category category);
}
