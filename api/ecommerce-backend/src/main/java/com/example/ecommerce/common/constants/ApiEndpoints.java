package com.example.ecommerce.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEndpoints {
    private static final String API_VERSION = "/api/v1";
    private static final String BASE_ADMIN = "/admin";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Category {
        public static final String BASE_CATEGORY = API_VERSION + "/categories";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Product {
        public static final String BASE_PRODUCT = API_VERSION + "/products";
        public static final String PRODUCT_INVENTORY = "{productId}/inventory";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cart {
        public static final String BASE_CART = API_VERSION + "/cart";
        public static final String ADD_CART_ITEM = "/items/{productId}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Payment {
        public static final String BASE_PAYMENT = API_VERSION + "/payments";
        public static final String CHECKOUT = "/checkout";
        public static final String SUCCESS = "/success";
        public static final String CANCEL = "/cancel";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Auth {
        public static final String BASE_AUTH = API_VERSION + "/auth";
        public static final String LOGIN = "/login";
        public static final String REGISTER = "/register";
        public static final String LOGOUT = "/logout";
        public static final String TOKEN_REFRESH = "/refresh";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class User {
        public static final String BASE_USER = API_VERSION + "/users";
        public static final String PROFILE = "/profile/{username}";
    }
}
