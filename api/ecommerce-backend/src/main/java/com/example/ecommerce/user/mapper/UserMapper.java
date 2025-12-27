package com.example.ecommerce.user.mapper;

import com.example.ecommerce.user.dto.request.UserRegistrationRequest;
import com.example.ecommerce.user.dto.response.RegisteredUserResponse;
import com.example.ecommerce.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserRegistrationRequest request);

    RegisteredUserResponse toResponse(User user);
}
