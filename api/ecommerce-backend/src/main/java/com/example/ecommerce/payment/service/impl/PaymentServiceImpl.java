package com.example.ecommerce.payment.service.impl;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.common.dto.request.StockReservationRequest;
import com.example.ecommerce.order.entity.Order;
import com.example.ecommerce.order.entity.OrderItem;
import com.example.ecommerce.order.enums.OrderStatus;
import com.example.ecommerce.order.service.OrderService;
import com.example.ecommerce.payment.config.StripeConfig;
import com.example.ecommerce.payment.entity.PaymentHistory;
import com.example.ecommerce.payment.enums.PaymentStatus;
import com.example.ecommerce.payment.repository.PaymentHistoryRepository;
import com.example.ecommerce.payment.service.PaymentService;
import com.example.ecommerce.product.service.InventoryService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PaymentService} for Stripe payment processing.
 *
 * <p>Orchestrates checkout flow including stock reservation, order creation,
 * and payment handling via Stripe webhooks.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final InventoryService inventoryService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final StripeConfig stripeConfig;

    @Transactional
    @Override
    public String checkout(Long userId) throws StripeException {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Step 1: Reserve stock
        List<StockReservationRequest> requests = cart.getItems().stream()
                .map(item -> StockReservationRequest.builder()
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        inventoryService.checkAndReserveStock(requests);

        // Step 2: Create order with NEW status
        Order order = orderService.createOrderFromCart(userId, cart);

        // Step 3: Clear cart
        cartService.clearCart(userId);

        // Step 4: Create Stripe session with order reference
        Session session = createStripeCheckoutSession(userId, order);

        // Step 5: Insert PaymentHistory
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .order(order)
                .sessionId(session.getId())
                .paymentLink(session.getUrl())
                .status(PaymentStatus.INITIATED)
                .build();

        paymentHistoryRepository.save(paymentHistory);

        return session.getUrl();
    }

    @Transactional
    @Override
    public void handleSuccessfulPayment(String sessionId) {
        Optional<PaymentHistory> paymentHistoryOptional = paymentHistoryRepository.findBySessionId(sessionId);
        if (paymentHistoryOptional.isEmpty()) {
            log.warn("No payment history found for session id = {}", sessionId);
            return;
        }

        PaymentHistory paymentHistory = paymentHistoryOptional.get();

        // Step 1: Update order status
        Order order = paymentHistory.getOrder();
        order.setStatus(OrderStatus.PAID);
        order = orderService.save(order);


        // Step 2: Update payment history status
        paymentHistory.setStatus(PaymentStatus.SUCCESS);
        paymentHistoryRepository.save(paymentHistory);

        // Step 3: Finalize reserved stock
        order.getItems().forEach(orderItem ->
                inventoryService.finalizeReservedStock(orderItem.getProductId(), orderItem.getQuantity()));
    }

    private Session createStripeCheckoutSession(Long userId, Order order) throws StripeException {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeConfig.getSuccessUrl())
                .setCancelUrl(stripeConfig.getCancelUrl());

        order.getItems().forEach(item -> paramsBuilder.addLineItem(createLineItem(item)));

        paramsBuilder.addLineItem(createDeliveryChargeLineItem(50));
        paramsBuilder.putMetadata("userId", String.valueOf(userId));

        return Session.create(paramsBuilder.build());
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem orderItem) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(orderItem.getQuantity().longValue())
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(stripeConfig.getCurrency())
                                .setUnitAmount((long) (orderItem.getUnitPrice() * 100))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(orderItem.getProductName())
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
