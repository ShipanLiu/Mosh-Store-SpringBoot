package com.codewithmosh.store.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessorService {

    @Autowired
    @Qualifier("creditCard") 
    private PaymentService creditCardPaymentService;
    
    @Autowired
    @Qualifier("payPal")
    private PaymentService payPalPaymentService;
    
    public void processPaymentWithCreditCard(double amount) {
        creditCardPaymentService.processPayment(amount);
    }
    
    public void processPaymentWithPayPal(double amount) {
        payPalPaymentService.processPayment(amount);
    }
    
    public void processPaymentWithPreferredMethod(double amount, boolean usePayPal) {
        if (usePayPal) {
            payPalPaymentService.processPayment(amount);
        } else {
            creditCardPaymentService.processPayment(amount);
        }
    }
} 