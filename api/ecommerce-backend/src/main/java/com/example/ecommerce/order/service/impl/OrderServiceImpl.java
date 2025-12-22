package com.example.ecommerce.order.service.impl;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.order.entity.Order;
import com.example.ecommerce.order.entity.OrderItem;
import com.example.ecommerce.order.enums.OrderStatus;
import com.example.ecommerce.order.mapper.OrderMapper;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Order checkout(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

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
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userId);

        return savedOrder;
    }
}
