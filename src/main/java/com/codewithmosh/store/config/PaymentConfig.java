package com.codewithmosh.store.config;

import com.codewithmosh.store.service.payment.CreditCardPaymentService;
import com.codewithmosh.store.service.payment.PayPalPaymentService;
import com.codewithmosh.store.service.payment.PaymentService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    @ConditionalOnProperty(name = "payment.method", havingValue = "creditCard", matchIfMissing = true)
    public PaymentService creditCardPaymentService() {
        return new CreditCardPaymentService();
    }

    @Bean
    @ConditionalOnProperty(name = "payment.method", havingValue = "paypal")
    public PaymentService payPalPaymentService() {
        return new PayPalPaymentService();
    }
} 