package com.example.ecommerce.user.mapper;

import com.example.ecommerce.user.dto.request.ProfileCreateRequest;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    UserProfile toEntity(ProfileCreateRequest request, User user);
}
