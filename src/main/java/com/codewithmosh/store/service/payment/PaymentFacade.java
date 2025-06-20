/*
* which payment service you want to use?
*
* */

package com.codewithmosh.store.service.payment;

import com.codewithmosh.store.service.payment.processors.PaymentService;
import com.codewithmosh.store.service.payment.processors.PayPalPaymentService;
import com.codewithmosh.store.service.payment.processors.CreditCardPaymentService;
import com.codewithmosh.store.service.payment.processors.StripePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Payment facade service that delegates payment processing to the configured payment provider.
 * Simple design with direct injection of all payment services.
 */
@Service
public class PaymentFacade {

    // Direct injection of all payment services
    private final PayPalPaymentService paypalService;
    private final CreditCardPaymentService creditCardService;
    private final StripePaymentService stripeService;
    
    // Current active payment service
    private PaymentService currentPaymentService;
    
    @Value("${payment.default-method:paypal}")
    private String defaultPaymentMethod;

    @Autowired
    public PaymentFacade(PayPalPaymentService paypalService, 
                        CreditCardPaymentService creditCardService, 
                        StripePaymentService stripeService) {
        this.paypalService = paypalService;
        this.creditCardService = creditCardService;
        this.stripeService = stripeService;
    }

    /**
     * Initialize the default payment service after all properties are injected
     */
    @PostConstruct
    public void initializeDefaultPaymentService() {
        System.out.println("Initializing PaymentFacade with default method: " + defaultPaymentMethod);
        setPaymentMethod(defaultPaymentMethod);
    }

    /**
     * Process a payment using the currently configured payment provider
     * @param amount the amount to process
     */
    public void processPayment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        
        if (currentPaymentService == null) {
            throw new IllegalStateException("No payment service configured");
        }
        
        currentPaymentService.processPayment(amount);
    }

    /**
     * Set the payment method - much simpler!
     */
    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }

        switch (paymentMethod.toLowerCase()) {
            case "paypal":
                currentPaymentService = paypalService;
                break;
            case "creditcard":
            case "credit-card":
                currentPaymentService = creditCardService;
                break;
            case "stripe":
                currentPaymentService = stripeService;
                break;
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod + 
                    ". Available methods: paypal, creditCard, stripe");
        }
        
        System.out.println("Payment method set to: " + paymentMethod);
    }

    /**
     * Get the current payment method name
     */
    public String getCurrentPaymentMethod() {
        if (currentPaymentService == null) {
            return "none";
        }
        
        if (currentPaymentService == paypalService) return "paypal";
        if (currentPaymentService == creditCardService) return "creditCard";
        if (currentPaymentService == stripeService) return "stripe";
        
        return "unknown";
    }

    /**
     * Get available payment methods
     */
    public List<String> getAvailablePaymentMethods() {
        return Arrays.asList("paypal", "creditCard", "stripe");
    }
} 