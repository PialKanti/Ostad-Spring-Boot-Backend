package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.request.CartRequest;
import com.example.ecommerce.cart.entity.Cart;

/**
 * Service interface for shopping cart operations.
 *
 * <p>Provides methods to add, update, retrieve, and clear
 * shopping cart items for users.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface CartService {

    /**
     * Adds a new item to cart or updates quantity if item exists.
     * Creates a new cart if user doesn't have one. Removes item if quantity is zero.
     *
     * @param productId the product ID to add/update
     * @param request contains user ID and desired quantity
     * @throws jakarta.persistence.EntityNotFoundException if product not found
     */
    void addOrUpdateCartItem(Long productId, CartRequest request);

    /**
     * Retrieves the cart for a specific user.
     *
     * @param userId the user ID
     * @return the user's cart with items
     * @throws jakarta.persistence.EntityNotFoundException if cart not found
     */
    Cart getCartByUserId(Long userId);

    /**
     * Removes all items from a user's cart.
     *
     * @param userId the user ID
     * @throws jakarta.persistence.EntityNotFoundException if cart not found
     */
    void clearCart(Long userId);
}
