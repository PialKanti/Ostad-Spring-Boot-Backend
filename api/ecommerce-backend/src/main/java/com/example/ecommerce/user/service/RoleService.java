package com.example.ecommerce.user.service;

import com.example.ecommerce.user.entity.Role;
import com.example.ecommerce.user.enums.RoleType;

import java.util.Optional;

/**
 * Service interface for role management.
 *
 * <p>Provides methods to find roles by their type code.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface RoleService {

    /**
     * Finds a role by its type code.
     *
     * @param code the role type (e.g., CUSTOMER, ADMIN)
     * @return optional containing the role if found
     */
    Optional<Role> findByCode(RoleType code);
}
