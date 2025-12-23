package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.request.CartRequest;
import com.example.ecommerce.cart.entity.Cart;

public interface CartService {
    void addOrUpdateCartItem(Long productId, CartRequest request);

    Cart getCartByUserId(Long userId);

    void clearCart(Long userId);

    double calculateSubTotalAmount(Cart cart);

    double calculateTotalAmount(Cart cart);
}
