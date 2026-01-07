package com.example.ecommerce.user.service;

import com.example.ecommerce.user.entity.Role;
import com.example.ecommerce.user.enums.RoleType;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByCode(RoleType code);
}
