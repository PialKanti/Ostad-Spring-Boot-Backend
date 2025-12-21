package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.dto.request.CartRequest;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.Cart.BASE_CART)
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping(ApiEndpoints.Cart.CART_ITEMS)
    public ResponseEntity<ApiResponse<Void>> createOrUpdateCart(@Valid @RequestBody CartRequest request) {
        cartService.addOrUpdateCartItems(request.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Items added to cart successfully."));
    }
}
