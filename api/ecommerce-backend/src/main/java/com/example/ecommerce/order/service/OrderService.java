package com.example.ecommerce.order.service;

import com.example.ecommerce.order.entity.Order;

public interface OrderService {
    Order checkout(Long userId);
}
