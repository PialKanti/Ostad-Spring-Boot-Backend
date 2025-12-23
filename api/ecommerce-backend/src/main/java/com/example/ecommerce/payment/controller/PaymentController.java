package com.example.ecommerce.payment.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.ApiResponse;
import com.example.ecommerce.payment.dto.response.CheckoutResponse;
import com.example.ecommerce.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.Payment.BASE_PAYMENT)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(ApiEndpoints.Payment.CHECKOUT)
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(@RequestParam(name = "userId") Long userId) throws StripeException {
        return ResponseEntity.ok(ApiResponse.success(
                CheckoutResponse.builder()
                        .checkoutUrl(paymentService.checkout(userId))
                        .build())
        );
    }

    @GetMapping(ApiEndpoints.Payment.SUCCESS)
    public ResponseEntity<ApiResponse<Void>> paymentSuccess(@RequestParam("session_id") String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);

        String userId = session.getMetadata().get("userId");
        paymentService.handleSuccessfulPayment(Long.valueOf(userId));

        return ResponseEntity.ok(ApiResponse.success("Payment successful! Session ID: " + sessionId));
    }

    @GetMapping(ApiEndpoints.Payment.CANCEL)
    public String paymentCancel() {
        return "Payment canceled.";
    }
}
