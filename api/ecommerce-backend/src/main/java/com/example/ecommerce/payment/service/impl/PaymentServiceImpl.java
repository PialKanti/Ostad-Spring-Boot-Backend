package com.example.ecommerce.payment.service.impl;

import com.example.ecommerce.cart.entity.Cart;
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

        return generatePaymentUrl(userId, cartService.calculateTotalAmount(cart));
    }

    @Transactional
    @Override
    public void handleSuccessfulPayment(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        orderService.createOrder(userId, cart);
    }

    private String generatePaymentUrl(Long userId, double totalAmount) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(stripeConfig.getSuccessUrl())
                        .setCancelUrl(stripeConfig.getCancelUrl())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency(stripeConfig.getCurrency())
                                                        .setUnitAmount((long) (totalAmount * 100))
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Demo Order")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .putMetadata("userId", String.valueOf(userId))
                        .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
