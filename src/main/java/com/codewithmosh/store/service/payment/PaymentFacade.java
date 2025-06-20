/*
* which payment service you want to use?
*
* */

package com.codewithmosh.store.service.payment;

import com.codewithmosh.store.service.payment.processors.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Payment facade service that delegates payment processing to the configured payment provider.
 * This service acts as a facade to abstract the specific payment implementation details
 * and allows dynamic switching between payment methods.
 */
@Service
public class PaymentFacade {

    private final ApplicationContext applicationContext;
    private final Map<String, PaymentService> paymentServices;
    private PaymentService currentPaymentService;
    
    @Value("${payment.default-method:paypal}")
    private String defaultPaymentMethod;

    @Autowired
    public PaymentFacade(ApplicationContext applicationContext, Map<String, PaymentService> paymentServices) {
        this.applicationContext = applicationContext;
        this.paymentServices = paymentServices;
        // Initialize with default payment method
        initializeDefaultPaymentService();
    }

    /**
     * Initialize the default payment service based on configuration
     */
    private void initializeDefaultPaymentService() {
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
     * Set the payment method dynamically
     * @param paymentMethod the payment method to use (paypal, stripe, creditCard)
     */
    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }

        PaymentService paymentService = paymentServices.get(paymentMethod.toLowerCase());
        
        if (paymentService == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod + 
                ". Available methods: " + paymentServices.keySet());
        }
        
        this.currentPaymentService = paymentService;
    }

    /**
     * Get the current payment method name
     * @return the current payment method name
     */
    public String getCurrentPaymentMethod() {
        if (currentPaymentService == null) {
            return "none";
        }
        
        // Find the bean name for the current service
        for (Map.Entry<String, PaymentService> entry : paymentServices.entrySet()) {
            if (entry.getValue() == currentPaymentService) {
                return entry.getKey();
            }
        }
        return "unknown";
    }

    /**
     * Get available payment methods
     * @return set of available payment method names
     */
    public java.util.Set<String> getAvailablePaymentMethods() {
        return paymentServices.keySet();
    }
} 