package com.codewithmosh.store.service.order;

import com.codewithmosh.store.service.payment.PaymentServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private PaymentServiceAPI paymentService;

    public OrderService() {}

    // here you use @Autowired because you have 2 constructors
    // if the interface PaymentService has 2 ikmplementations, you need to use @Qualifier (like to set a service to default)
    @Autowired
    public OrderService(@Qualifier("stripe") PaymentServiceAPI paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder(double amount) {
        paymentService.processPayment(amount);
    }

    public void setPaymentService(PaymentServiceAPI paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentServiceAPI getPaymentService() {
        return paymentService;
    }
}
