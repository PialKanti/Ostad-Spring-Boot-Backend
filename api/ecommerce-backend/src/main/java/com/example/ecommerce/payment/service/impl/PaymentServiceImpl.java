package com.example.ecommerce.payment.service.impl;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.entity.CartItem;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.order.service.OrderService;
import com.example.ecommerce.payment.config.StripeConfig;
import com.example.ecommerce.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final CartService cartService;
    private final OrderService orderService;
    private final StripeConfig stripeConfig;

    @Override
    public String checkout(Long userId) throws StripeException {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        return createStripeCheckoutSession(userId, cart);
    }

    @Transactional
    @Override
    public void handleSuccessfulPayment(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        orderService.createOrder(userId, cart);

        cartService.clearCart(userId);
    }

    private String createStripeCheckoutSession(Long userId, Cart cart) throws StripeException {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeConfig.getSuccessUrl())
                .setCancelUrl(stripeConfig.getCancelUrl());

        cart.getItems().forEach(item -> paramsBuilder.addLineItem(createLineItem(item)));

        paramsBuilder.addLineItem(createDeliveryChargeLineItem(50));
        paramsBuilder.putMetadata("userId", String.valueOf(userId));

        Session session = Session.create(paramsBuilder.build());
        return session.getUrl();
    }

    private SessionCreateParams.LineItem createLineItem(CartItem cartItem) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(cartItem.getQuantity().longValue())
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(stripeConfig.getCurrency())
                                .setUnitAmount((long) (cartItem.getUnitPrice() * 100))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(cartItem.getProduct().getName())
                                                .setDescription(cartItem.getProduct().getDescription())
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private SessionCreateParams.LineItem createDeliveryChargeLineItem(double deliveryCharge) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(stripeConfig.getCurrency())
                                .setUnitAmount((long) (deliveryCharge * 100))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Delivery Charge")
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
