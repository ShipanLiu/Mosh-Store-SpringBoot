package com.codewithmosh.store.service.payment.processors;

import com.codewithmosh.store.config.StripeConfig;
import org.springframework.stereotype.Service;

@Service("stripe")
public class StripePaymentService implements PaymentService {

    // We pack all the strip-info into a class StripeConfig
    private final StripeConfig stripeConfig;

    public StripePaymentService(StripeConfig stripeConfig) {
        this.stripeConfig = stripeConfig;
    }

    @Override
    public void processPayment(double amount) {
        if (!stripeConfig.isEnabled()) {
            throw new IllegalStateException("Stripe payment service is disabled");
        }

        System.out.println("=== Stripe Payment Processing ===");
        System.out.println("Processing Stripe payment of $" + amount);
        System.out.println("Stripe API URL: " + stripeConfig.getApiUrl());
        System.out.println("Timeout: " + stripeConfig.getTimeout() + "ms");
        System.out.println("Stripe Status: " + (stripeConfig.isEnabled() ? "ACTIVE" : "DISABLED"));
        System.out.println("Supported currencies: " + stripeConfig.getSupportedCurrencies());
        
        // Simulate Stripe API processing
        simulateStripeApiCall(amount);
        
        System.out.println("Stripe payment completed successfully!");
        System.out.println("===================================");
    }

    private void simulateStripeApiCall(double amount) {
        try {
            System.out.println("Connecting to Stripe API...");
            Thread.sleep(90); // Simulate network delay
            
            System.out.println("Creating payment intent...");
            Thread.sleep(60);
            
            System.out.println("Processing payment...");
            Thread.sleep(110);
            
            // Generate realistic Stripe transaction details
            String paymentIntentId = "pi_" + System.currentTimeMillis() + "_secret";
            String chargeId = "ch_" + System.currentTimeMillis();
            
            System.out.println("Payment Intent ID: " + paymentIntentId);
            System.out.println("Charge ID: " + chargeId);
            System.out.println("Stripe Fee: $" + (amount * 0.029 + 0.30)); // Stripe's fee structure
            System.out.println("Currency: USD");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Stripe API call interrupted", e);
        }
    }
}
