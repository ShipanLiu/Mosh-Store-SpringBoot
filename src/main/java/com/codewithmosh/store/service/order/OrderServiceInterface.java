package com.codewithmosh.store.service.order;

import com.codewithmosh.store.service.payment.processors.PaymentService;

public interface OrderServiceInterface {
    void placeOrder(double amount);

    void setPaymentService(PaymentService paymentService);

    PaymentService getPaymentService();
}
