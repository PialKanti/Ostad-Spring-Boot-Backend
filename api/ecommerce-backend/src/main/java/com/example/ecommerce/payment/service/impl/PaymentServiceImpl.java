package com.example.ecommerce.payment.service.impl;

import com.example.ecommerce.cart.entity.Cart;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.common.dto.request.StockReservationRequest;
import com.example.ecommerce.inventory.dto.ReservationResponse;
import com.example.ecommerce.inventory.service.InventoryClientService;
import com.example.ecommerce.order.entity.Order;
import com.example.ecommerce.order.entity.OrderItem;
import com.example.ecommerce.order.entity.OrderReservation;
import com.example.ecommerce.order.enums.OrderStatus;
import com.example.ecommerce.order.enums.ReservationStatus;
import com.example.ecommerce.order.repository.OrderReservationRepository;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.order.service.OrderService;
import com.example.ecommerce.payment.config.StripeConfig;
import com.example.ecommerce.payment.entity.PaymentHistory;
import com.example.ecommerce.payment.enums.PaymentStatus;
import com.example.ecommerce.payment.repository.PaymentHistoryRepository;
import com.example.ecommerce.payment.service.PaymentService;
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
    private final InventoryClientService inventoryClientService;
    private final CartService cartService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderReservationRepository orderReservationRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final StripeConfig stripeConfig;

    @Transactional
    @Override
    public String checkout(Long userId) throws StripeException {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Step 1: Create order FIRST (need order ID for reservations)
        Order order = orderService.createOrderFromCart(userId, cart);

        // Step 2: Build stock reservation requests
        List<StockReservationRequest> requests = cart.getItems().stream()
                .map(item -> StockReservationRequest.builder()
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        // Step 3: Reserve stock via inventory microservice
        List<ReservationResponse> reservations;
        try {
            reservations = inventoryClientService.reserveStockForOrder(requests, order.getId());
        } catch (Exception e) {
            log.error("Stock reservation failed for order {}, deleting order", order.getId(), e);
            orderRepository.delete(order);
            throw e;
        }

        // Step 4: Save reservation IDs to OrderReservation table
        for (ReservationResponse reservation : reservations) {
            OrderReservation orderReservation = OrderReservation.builder()
                    .order(order)
                    .productId(reservation.getProductId())
                    .reservationId(reservation.getId())
                    .quantity(reservation.getQuantity())
                    .status(ReservationStatus.PENDING)
                    .build();
            orderReservationRepository.save(orderReservation);
        }

        // Step 5: Clear cart
        cartService.clearCart(userId);

        // Step 6: Create Stripe session with order reference
        Session session = createStripeCheckoutSession(userId, order);

        // Step 7: Insert PaymentHistory
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

        // Step 3: Get reservation IDs from OrderReservation table and confirm them
        List<OrderReservation> orderReservations = orderReservationRepository
                .findByOrderIdAndStatus(order.getId(), ReservationStatus.PENDING);

        List<Long> reservationIds = orderReservations.stream()
                .map(OrderReservation::getReservationId)
                .toList();

        inventoryClientService.confirmReservations(reservationIds);

        // Step 4: Update reservation status to CONFIRMED
        for (OrderReservation reservation : orderReservations) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            orderReservationRepository.save(reservation);
        }
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
