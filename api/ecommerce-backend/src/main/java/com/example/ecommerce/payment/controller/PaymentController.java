package com.example.ecommerce.payment.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.payment.config.StripeConfig;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.Payment.BASE_PAYMENT)
@RequiredArgsConstructor
public class PaymentController {
    private final StripeConfig stripeConfig;

    @PostMapping(ApiEndpoints.Payment.CHECKOUT)
    public ResponseEntity<String> checkout() throws StripeException {

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
                                                        .setCurrency("bdt")
                                                        .setUnitAmount(1000L * 100)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Demo Order")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        return ResponseEntity.ok(session.getUrl()); // Stripe-hosted page
    }

    @GetMapping(ApiEndpoints.Payment.SUCCESS)
    public String paymentSuccess(@RequestParam("session_id") String sessionId) {
        // Optional: fetch session info from Stripe API
        return "Payment successful! Session ID: " + sessionId;
    }

    @GetMapping(ApiEndpoints.Payment.CANCEL)
    public void paymentCancel() {
        System.out.println("Payment canceled.");
    }
}
