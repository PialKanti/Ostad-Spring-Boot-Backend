package com.example.ecommerce.order.service;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.order.entity.Order;

public interface OrderService {
    Order createOrderFromCart(Long userId, Cart cart);

    Order save(Order order);
}
