package com.example.ecommerce.payment.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.response.ApiResponse;
import com.example.ecommerce.payment.dto.response.CheckoutResponse;
import com.example.ecommerce.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.Payment.BASE_PAYMENT)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(ApiEndpoints.Payment.CHECKOUT)
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(@RequestParam(name = "user_id") Long userId) throws StripeException {
        return ResponseEntity.ok(ApiResponse.success(
                CheckoutResponse.builder()
                        .checkoutUrl(paymentService.checkout(userId))
                        .build())
        );
    }

    @GetMapping(ApiEndpoints.Payment.SUCCESS)
    public ResponseEntity<ApiResponse<Void>> paymentSuccess(@RequestParam("session_id") String sessionId) {
        paymentService.handleSuccessfulPayment(sessionId);

        return ResponseEntity.ok(ApiResponse.success("Payment successful! Session ID: " + sessionId));
    }

    @GetMapping(ApiEndpoints.Payment.CANCEL)
    public String paymentCancel() {
        return "Payment canceled.";
    }
}
