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

@Service
public class PaymentFacade {

    private static final String PAYPAL_PAYMENT_NAME = "paypal";
    private static final String CREDIT_CARD_PAYMENT_NAME = "credit-card";
    private static final String STRIPE_PAYMENT_NAME = "stripe";


    private final PayPalPaymentService paypalService;
    private final CreditCardPaymentService creditCardService;
    private final StripePaymentService stripeService;
    
    // Current active payment service
    private PaymentService currentPaymentService;
    
    @Value("${payment.default-method:paypal}")
    private String defaultPaymentMethod;
    private String currentPaymentMethod = defaultPaymentMethod;

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
            case PAYPAL_PAYMENT_NAME:
                currentPaymentService = paypalService;
                currentPaymentMethod = PAYPAL_PAYMENT_NAME;
                break;
            case CREDIT_CARD_PAYMENT_NAME:
                currentPaymentService = creditCardService;
                currentPaymentMethod = CREDIT_CARD_PAYMENT_NAME;
                break;
            case STRIPE_PAYMENT_NAME:
                currentPaymentService = stripeService;
                currentPaymentMethod = STRIPE_PAYMENT_NAME;
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
        return currentPaymentMethod;
    }

    /**
     * Get available payment methods
     */
    public List<String> getAvailablePaymentMethods() {
        return Arrays.asList("paypal", "creditCard", "stripe");
    }
} 