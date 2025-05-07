package com.codewithmosh.store.service.order;

import com.codewithmosh.store.service.payment.PaymentServiceAPI;

public interface OrderServiceInterface {
    void placeOrder(double amount);

    void setPaymentService(PaymentServiceAPI paymentService);

    PaymentServiceAPI getPaymentService();
}
