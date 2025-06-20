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

    public void processPayment(double amount) {
        System.out.println("Stripe payment was processed with amount: " + amount);
        System.out.println("API URL: " + stripeConfig.getApiUrl());
        System.out.println("Enabled: " + stripeConfig.isEnabled());
        System.out.println("Timeout: " + stripeConfig.getTimeout());
        System.out.println("Supported currencies: " + stripeConfig.getSupportedCurrencies());
    }
}
