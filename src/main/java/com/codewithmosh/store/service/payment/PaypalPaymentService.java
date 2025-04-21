package com.codewithmosh.store.service.payment;

import org.springframework.stereotype.Service;

@Service("paypal")
public class PaypalPaymentService implements PaymentServiceAPI {
    @Override
    public void processPayment(double amount) {
        System.out.println("paypal payment was processed with amount + " + amount);
    }
}
