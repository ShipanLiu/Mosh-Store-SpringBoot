package com.codewithmosh.store.config;

import com.codewithmosh.store.service.payment.processors.StripePaymentService;
import com.codewithmosh.store.service.payment.processors.CreditCardPaymentService;
import com.codewithmosh.store.service.payment.processors.PayPalPaymentService;
import com.codewithmosh.store.service.payment.processors.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Payment configuration that creates all payment service beans.
 * This allows for dynamic switching between payment methods at runtime.
 * Each payment service is properly configured with its respective configuration class.
 */
@Configuration
public class PaymentConfig {

    /**
     * Credit Card payment service bean with configuration injection
     */
    @Bean("creditCard")
    public PaymentService creditCardPaymentService(CreditCardConfig creditCardConfig) {
        return new CreditCardPaymentService(creditCardConfig);
    }

    /**
     * PayPal payment service bean with configuration injection
     */
    @Bean("paypal")
    public PaymentService payPalPaymentService(PayPalConfig payPalConfig) {
        return new PayPalPaymentService(payPalConfig);
    }

    /**
     * Stripe payment service bean with configuration injection
     */
    @Bean("stripe")
    public PaymentService stripePaymentService(StripeConfig stripeConfig) {
        return new StripePaymentService(stripeConfig);
    }
} 