package com.example.ecommerce.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    SUPER_ADMIN("Super Admin"),
    ADMIN("Admin"),
    USER("User");

    private final String displayValue;
}
