package com.example.ecommerce.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    // Product Admin
    CREATE_PRODUCT("Create Product"),
    UPDATE_PRODUCT("Update Product"),
    DELETE_PRODUCT("Delete Product"),
    UPDATE_INVENTORY("Update Inventory"),

    // Category Admin
    CREATE_CATEGORY("Create Category"),
    UPDATE_CATEGORY("Update Category"),
    DELETE_CATEGORY("Delete Category"),
    TOGGLE_CATEGORY_STATUS("Toggle Category Status"),
    VIEW_CATEGORY("View Category"),

    // Customer / General
    VIEW_PRODUCTS("View Products"),
    VIEW_CART("View Cart"),
    ADD_TO_CART("Add To Cart"),

    // Payments
    CHECKOUT("Checkout"),
    PAYMENT_SUCCESS("Payment Success"),
    PAYMENT_CANCEL("Payment Cancel");

    private final String displayValue;
}
