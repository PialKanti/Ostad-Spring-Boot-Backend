package com.example.ecommerce.payment.controller;

import com.example.ecommerce.common.constants.ApiEndpoints;
import com.example.ecommerce.common.dto.response.ApiResponse;
import com.example.ecommerce.payment.dto.response.CheckoutResponse;
import com.example.ecommerce.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.Payment.BASE_PAYMENT)
@RequiredArgsConstructor
@Tag(
        name = "Payment",
        description = "Operations for handling payments and Stripe integration"
)
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(
            summary = "Initiate checkout process",
            description = "Creates a Stripe checkout session and returns the URL for the user to complete payment.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Checkout session created successfully",
                            content = @Content(schema = @Schema(implementation = CheckoutResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid user ID or bad request",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Stripe integration error",
                            content = @Content
                    )
            }
    )
    @PostMapping(ApiEndpoints.Payment.CHECKOUT)
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(@RequestParam(name = "user_id") Long userId) throws StripeException {
        return ResponseEntity.ok(ApiResponse.success(
                CheckoutResponse.builder()
                        .checkoutUrl(paymentService.checkout(userId))
                        .build())
        );
    }

    @Operation(
            summary = "Handle successful payment",
            description = "Callback endpoint for Stripe to report successful payment. Updates order status and inventory.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment processed successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Session ID not found",
                            content = @Content
                    )
            }
    )
    @GetMapping(ApiEndpoints.Payment.SUCCESS)
    public ResponseEntity<ApiResponse<Void>> paymentSuccess(@RequestParam("session_id") String sessionId) {
        paymentService.handleSuccessfulPayment(sessionId);

        return ResponseEntity.ok(ApiResponse.success("Payment successful! Session ID: " + sessionId));
    }

    @Operation(
            summary = "Handle canceled payment",
            description = "Callback endpoint for Stripe when payment is canceled by the user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Cancellation handled successfully"
                    )
            }
    )
    @GetMapping(ApiEndpoints.Payment.CANCEL)
    public String paymentCancel() {
        return "Payment canceled.";
    }
}
