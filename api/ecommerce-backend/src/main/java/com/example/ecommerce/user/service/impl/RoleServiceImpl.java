package com.example.ecommerce.user.service.impl;

import com.example.ecommerce.user.entity.Role;
import com.example.ecommerce.user.enums.RoleType;
import com.example.ecommerce.user.repository.RoleRepository;
import com.example.ecommerce.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByCode(RoleType code) {
        return roleRepository.findByCode(code);
    }
}
