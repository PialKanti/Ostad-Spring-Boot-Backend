package com.example.ecommerce.cart.service.impl;

import com.example.ecommerce.cart.dto.request.CartRequest;
import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.entity.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.product.entity.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void addOrUpdateCartItem(Long productId, CartRequest request) {
        Cart cart = cartRepository.findByUserId(request.userId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(request.userId())
                                .build())
                );


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        CartItem existingItem = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct()
                        .getId().equals(product.getId())).findFirst().orElse(null);


        if (existingItem != null) {
            if (request.quantity() == 0) {
                cart.getItems().remove(existingItem);
            } else {
                existingItem.setQuantity(request.quantity());
            }
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .unitPrice(product.getPrice())
                    .build();

            cart.getItems().add(newItem);
        }


        cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user: " + userId));
    }
}
