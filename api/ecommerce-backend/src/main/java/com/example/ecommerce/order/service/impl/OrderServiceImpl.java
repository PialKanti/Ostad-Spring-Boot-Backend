package com.example.ecommerce.order.service.impl;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.order.entity.Order;
import com.example.ecommerce.order.entity.OrderItem;
import com.example.ecommerce.order.enums.OrderStatus;
import com.example.ecommerce.order.mapper.OrderMapper;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link OrderService} for order processing.
 *
 * <p>Handles order creation from cart with pricing calculations
 * including subtotal, discount, and delivery charges.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order createOrderFromCart(Long userId, Cart cart) {
        double subtotal = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        double discount = 0.0;
        double deliveryCharge = 50.0;
        double totalPrice = subtotal - discount + deliveryCharge;

        Order order = Order.builder()
                .userId(userId)
                .subTotal(subtotal)
                .discountAmount(discount)
                .deliveryCharge(deliveryCharge)
                .totalPrice(totalPrice)
                .status(OrderStatus.NEW)
                .build();

        List<OrderItem> orderItems = orderMapper.toOrderItems(cart.getItems());
        orderItems.forEach(item -> item.setOrder(order));

        order.setItems(orderItems);
        return orderRepository.save(order);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
