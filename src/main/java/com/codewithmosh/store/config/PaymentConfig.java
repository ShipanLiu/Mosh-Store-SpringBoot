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
 */
@Configuration
public class PaymentConfig {

    /**
     * Credit Card payment service bean
     */
    @Bean("creditCard")
    public PaymentService creditCardPaymentService() {
        return new CreditCardPaymentService();
    }

    /**
     * PayPal payment service bean
     */
    @Bean("paypal")
    public PaymentService payPalPaymentService() {
        return new PayPalPaymentService();
    }

    /**
     * Stripe payment service bean
     */
    @Bean("stripe")
    public PaymentService stripePaymentService(StripeConfig stripeConfig) {
        return new StripePaymentService(stripeConfig);
    }
} 