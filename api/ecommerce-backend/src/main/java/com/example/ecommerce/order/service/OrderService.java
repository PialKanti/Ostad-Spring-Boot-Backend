package com.example.ecommerce.order.service;

import com.example.ecommerce.cart.entity.Cart;

public interface OrderService {
    void createOrder(Long userId, Cart cart);
}
