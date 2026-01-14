package com.example.ecommerce.order.service;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.order.entity.Order;

/**
 * Service interface for order management operations.
 *
 * <p>Provides methods to create orders from cart contents
 * and persist order entities.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface OrderService {

    /**
     * Creates a new order from cart contents with calculated totals.
     * Applies subtotal, discount, and delivery charges.
     *
     * @param userId the user placing the order
     * @param cart the cart containing items to order
     * @return the created order with NEW status
     */
    Order createOrderFromCart(Long userId, Cart cart);

    /**
     * Persists an order entity.
     *
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);
}
