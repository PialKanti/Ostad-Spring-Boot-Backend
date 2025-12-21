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
    public void addOrUpdateCartItems(Long userId, CartRequest request) {
        Cart cart = cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .isActive(true)
                            .build();

                    return cartRepository.save(newCart);
                });

        for (CartRequest.CartItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemRequest.productId()));

            CartItem existingItem = cart.getItems().stream()
                    .filter(cartItem -> cartItem.getProduct()
                            .getId().equals(product.getId())).findFirst().orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(itemRequest.quantity());
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(itemRequest.quantity());
                cart.getItems().add(newItem);
            }
        }

        cartRepository.save(cart);
    }
}
