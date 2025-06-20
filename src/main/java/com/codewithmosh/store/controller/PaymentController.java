package com.codewithmosh.store.controller;

import com.codewithmosh.store.service.payment.PaymentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @Autowired
    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    /**
     * Process a payment with the current payment method
     */
    @GetMapping("/process/{amount}")
    public ResponseEntity<Map<String, Object>> processPayment(@PathVariable double amount) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String currentMethod = paymentFacade.getCurrentPaymentMethod();
            paymentFacade.processPayment(amount);
            
            response.put("success", true);
            response.put("message", "Payment of $" + amount + " processed successfully");
            response.put("paymentMethod", currentMethod);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Set the payment method dynamically
     */
    @PostMapping("/method/{paymentMethod}")
    public ResponseEntity<Map<String, Object>> setPaymentMethod(@PathVariable String paymentMethod) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            paymentFacade.setPaymentMethod(paymentMethod);
            
            response.put("success", true);
            response.put("message", "Payment method set to: " + paymentMethod);
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
     * Process payment with a specific method (without changing the default)
     */
    @PostMapping("/process/{amount}/with/{paymentMethod}")
    public ResponseEntity<Map<String, Object>> processPaymentWithMethod(
            @PathVariable double amount, 
            @PathVariable String paymentMethod) {
        
        Map<String, Object> response = new HashMap<>();
        String originalMethod = paymentFacade.getCurrentPaymentMethod();
        
        try {
            // Temporarily switch to the specified method
            paymentFacade.setPaymentMethod(paymentMethod);
            paymentFacade.processPayment(amount);
            
            response.put("success", true);
            response.put("message", "Payment of $" + amount + " processed successfully with " + paymentMethod);
            response.put("paymentMethod", paymentMethod);
            
            // Restore original method
            paymentFacade.setPaymentMethod(originalMethod);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Restore original method in case of error
            try {
                paymentFacade.setPaymentMethod(originalMethod);
            } catch (Exception restoreException) {
                // Log the restore exception but don't override the main error
            }
            
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("availableMethods", paymentFacade.getAvailablePaymentMethods());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 