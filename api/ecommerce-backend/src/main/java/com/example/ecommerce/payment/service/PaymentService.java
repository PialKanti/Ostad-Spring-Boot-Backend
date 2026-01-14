package com.example.ecommerce.payment.service;

import com.stripe.exception.StripeException;

/**
 * Service interface for payment processing with Stripe integration.
 *
 * <p>Provides methods to initiate checkout and handle
 * successful payment webhooks.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface PaymentService {

    /**
     * Initiates checkout process: reserves stock, creates order, and generates Stripe session.
     *
     * @param userId the user initiating checkout
     * @return the Stripe checkout URL for payment
     * @throws StripeException if Stripe session creation fails
     * @throws IllegalStateException if cart is empty
     * @throws com.example.ecommerce.common.exception.OutOfStockException if insufficient stock
     */
    String checkout(Long userId) throws StripeException;

    /**
     * Handles successful payment webhook: updates order status, payment history, and finalizes stock.
     *
     * @param sessionId the Stripe session ID from webhook
     */
    void handleSuccessfulPayment(String sessionId);
}
