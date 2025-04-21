package com.codewithmosh.store.config;

import com.codewithmosh.store.service.order.OrderService;
import com.codewithmosh.store.service.payment.PaypalPaymentService;
import com.codewithmosh.store.service.payment.StripePaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final StripeConfig stripeConfig;

    public AppConfig(StripeConfig stripeConfig) {
        this.stripeConfig = stripeConfig;
    }

    // if you don't use the @Service, you can create the bean here, the retured value will be injected into the constructor of the OrderService.
    @Bean(name = "paypal")
    public PaypalPaymentService paypalPaymentService() {
        // if ... else ...
        return new PaypalPaymentService();
    }

    // if you don't use the @Service, you can create the bean here.
    @Bean(name = "stripe")
    public StripePaymentService stripePaymentService() {
        return new StripePaymentService(stripeConfig);
    }

    // if you don't use the @Service, you can create the bean here.
    @Bean
    public OrderService orderService() {
        return new OrderService(stripePaymentService());
    }


}
