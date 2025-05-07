package com.codewithmosh.store.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(@Qualifier("payPal") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/process/{amount}")
    public String processPayment(@PathVariable double amount) {
        paymentService.processPayment(amount);
        return "Payment processed";
    }
} 