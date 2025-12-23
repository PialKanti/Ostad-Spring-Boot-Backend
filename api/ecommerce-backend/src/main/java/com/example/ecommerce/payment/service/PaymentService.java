package com.example.ecommerce.payment.service;

import com.stripe.exception.StripeException;

public interface PaymentService {
    String checkout(Long userId) throws StripeException;

    void handleSuccessfulPayment(Long userId);
}
