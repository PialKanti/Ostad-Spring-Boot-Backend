package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.request.CartRequest;

public interface CartService {
    void addOrUpdateCartItems(Long userId, CartRequest request);
}
