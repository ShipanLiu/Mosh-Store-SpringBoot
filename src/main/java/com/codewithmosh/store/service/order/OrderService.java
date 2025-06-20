package com.codewithmosh.store.service.order;

import com.codewithmosh.store.service.payment.processors.PaymentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements OrderServiceInterface {

    private PaymentService paymentService;

    public OrderService() {}

    // here you use @Autowired because you have 2 constructors
    // if the interface PaymentService has 2 implementations, you need to use @Qualifier (like to set a service to default)
    @Autowired
    public OrderService(@Qualifier("stripe") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostConstruct
    public void init() {
        // create DB connection ...
        System.out.println("@PostConstruct before initing the  bean");
    }


    @PreDestroy
    public void cleanup() {
        // release DB connections
        // file handles
        // clean threads
        System.out.println("this method will be called only after the application context be ended");
        System.out.println("@PreDestroy before destorying the bean");
    }

    @Override
    public void placeOrder(double amount) {
        paymentService.processPayment(amount);
    }

    @Override
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public PaymentService getPaymentService() {
        return paymentService;
    }
}
