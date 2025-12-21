package com.example.ecommerce.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEndpoints {
    private static final String API_VERSION = "/api/v1";
    private static final String BASE_ADMIN = "/admin";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CategoryAdmin {
        public static final String BASE_CATEGORY_ADMIN = API_VERSION + BASE_ADMIN + "/categories";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProductAdmin {
        public static final String BASE_PRODUCT_ADMIN = API_VERSION + BASE_ADMIN + "/products";
        public static final String PRODUCT_INVENTORY = "{productId}/inventory";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cart {
        public static final String BASE_CART = API_VERSION + "/cart";
        public static final String CART_ITEMS = BASE_CART + "/items";
    }
}
