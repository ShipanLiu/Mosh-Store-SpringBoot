/*

This is just for a show


*
* Demo：  if you don't use the annotations, create ean manually
*
* *//*


package com.codewithmosh.store.config.deprecated;

import com.codewithmosh.store.config.StripeConfig;
import com.codewithmosh.store.service.order.OrderService;
import com.codewithmosh.store.service.payment.processors.PayPalPaymentService;
import com.codewithmosh.store.service.payment.processors.StripePaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private final StripeConfig stripeConfig;

    @Value("${payment-gateway:stripe}")
    private String paymentGateway;

    public AppConfig(StripeConfig stripeConfig) {
        this.stripeConfig = stripeConfig;
    }

    // if you don't use the @Service, you can create the bean here, the retured value will be injected into the constructor of the OrderService.
    @Bean(name = "paypal")
    public PayPalPaymentService paypalPaymentService() {
        // if ... else ...
        return new PayPalPaymentService();
    }

    // if you don't use the @Service, you can create the bean here.
    @Bean(name = "stripe")
    public StripePaymentService stripePaymentService() {
        return new StripePaymentService(stripeConfig);
    }

    // if you don't use the @Service, you can create the bean here.
    @Bean(name = "orderService")
    public OrderService orderService() {

        if(paymentGateway.equals("paypal")) {
            return new OrderService(new PayPalPaymentService());
        }

        return new OrderService(stripePaymentService());
    }
}
*/
