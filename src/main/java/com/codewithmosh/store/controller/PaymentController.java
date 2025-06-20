package com.codewithmosh.store.controller;

import com.codewithmosh.store.service.payment.PaymentFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@Tag(name = "Payment", description = "Payment processing and management API")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @Autowired
    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    /**
     * Process a payment using request parameters (NOT path variables!)
     * POST /payment/process?amount=100.00&method=paypal
     */
    @Operation(summary = "Process Payment", 
               description = "Process a payment with specified amount and optional payment method")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment parameters")
    })
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(
            @Parameter(description = "Payment amount", required = true, example = "100.00")
            @RequestParam double amount,
            @Parameter(description = "Payment method (optional)", example = "paypal")
            @RequestParam(required = false) String method) {
        
        Map<String, Object> response = new HashMap<>();
        String originalMethod = paymentFacade.getCurrentPaymentMethod();
        
        try {
            // If method is specified, temporarily switch to it
            if (method != null && !method.trim().isEmpty()) {
                paymentFacade.setPaymentMethod(method);
            }
            
            String currentMethod = paymentFacade.getCurrentPaymentMethod();
            paymentFacade.processPayment(amount);
            
            response.put("success", true);
            response.put("message", "Payment of $" + amount + " processed successfully");
            response.put("paymentMethod", currentMethod);
            response.put("amount", amount);
            
            // Restore original method if we temporarily switched
            if (method != null && !method.trim().isEmpty()) {
                paymentFacade.setPaymentMethod(originalMethod);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Restore original method in case of error
            if (method != null && !method.trim().isEmpty()) {
                try {
                    paymentFacade.setPaymentMethod(originalMethod);
                } catch (Exception restoreException) {
                    // Log but don't override main error
                }
            }
            
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("availableMethods", paymentFacade.getAvailablePaymentMethods());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Process payment with JSON request body
     * POST /payment/process
     * Body: {"amount": 100.00, "method": "paypal"}
     */
    @PostMapping("/process-json")
    public ResponseEntity<Map<String, Object>> processPaymentJson(@RequestBody PaymentRequest request) {
        Map<String, Object> response = new HashMap<>();
        String originalMethod = paymentFacade.getCurrentPaymentMethod();
        
        try {
            // If method is specified, temporarily switch to it
            if (request.getMethod() != null && !request.getMethod().trim().isEmpty()) {
                paymentFacade.setPaymentMethod(request.getMethod());
            }
            
            String currentMethod = paymentFacade.getCurrentPaymentMethod();
            paymentFacade.processPayment(request.getAmount());
            
            response.put("success", true);
            response.put("message", "Payment of $" + request.getAmount() + " processed successfully");
            response.put("paymentMethod", currentMethod);
            response.put("amount", request.getAmount());
            
            // Restore original method if we temporarily switched
            if (request.getMethod() != null && !request.getMethod().trim().isEmpty()) {
                paymentFacade.setPaymentMethod(originalMethod);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Restore original method in case of error
            if (request.getMethod() != null && !request.getMethod().trim().isEmpty()) {
                try {
                    paymentFacade.setPaymentMethod(originalMethod);
                } catch (Exception restoreException) {
                    // Log but don't override main error
                }
            }
            
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("availableMethods", paymentFacade.getAvailablePaymentMethods());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Set the default payment method using request parameter
     * POST /payment/method?method=stripe
     */
    @PostMapping("/method")
    public ResponseEntity<Map<String, Object>> setPaymentMethod(@RequestParam String method) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            paymentFacade.setPaymentMethod(method);
            
            response.put("success", true);
            response.put("message", "Payment method set to: " + method);
            response.put("currentMethod", paymentFacade.getCurrentPaymentMethod());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("availableMethods", paymentFacade.getAvailablePaymentMethods());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get current payment method and available options
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getPaymentInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("currentMethod", paymentFacade.getCurrentPaymentMethod());
        response.put("availableMethods", paymentFacade.getAvailablePaymentMethods());
        return ResponseEntity.ok(response);
    }

    /**
     * Payment request DTO for JSON requests
     */
    public static class PaymentRequest {
        private double amount;
        private String method;

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
    }
} 