package com.codewithmosh.store.service.payment.processors;

import org.springframework.stereotype.Service;

@Service("creditCard")
public class CreditCardPaymentService implements PaymentService {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
} 