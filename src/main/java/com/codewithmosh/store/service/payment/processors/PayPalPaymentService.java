package com.codewithmosh.store.service.payment.processors;

import com.codewithmosh.store.config.PayPalConfig;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("paypal")
@Primary
public class PayPalPaymentService implements PaymentService {

    private final PayPalConfig payPalConfig;

    public PayPalPaymentService(PayPalConfig payPalConfig) {
        this.payPalConfig = payPalConfig;
    }

    @Override
    public void processPayment(double amount) {
        if (!payPalConfig.isEnabled()) {
            throw new IllegalStateException("PayPal payment service is disabled");
        }

        System.out.println("=== PayPal Payment Processing ===");
        System.out.println("Processing PayPal payment of $" + amount);
        System.out.println("PayPal API URL: " + payPalConfig.getApiUrl());
        System.out.println("Environment: " + payPalConfig.getEnvironment());
        System.out.println("Timeout: " + payPalConfig.getTimeout() + "ms");
        System.out.println("PayPal Status: " + (payPalConfig.isEnabled() ? "ACTIVE" : "DISABLED"));
        System.out.println("Supported Currencies: " + payPalConfig.getSupportedCurrencies());
        
        // Simulate API call
        simulatePayPalApiCall(amount);
        
        System.out.println("PayPal payment completed successfully!");
        System.out.println("=====================================");
    }

    private void simulatePayPalApiCall(double amount) {
        try {
            System.out.println("Connecting to PayPal API...");
            System.out.println("Using Client ID: " + maskClientId(payPalConfig.getClientId()));
            Thread.sleep(100); // Simulate network delay
            
            System.out.println("Creating PayPal order...");
            Thread.sleep(80);
            
            System.out.println("Authorizing payment...");
            Thread.sleep(120);
            
            // Generate realistic PayPal transaction details
            String orderId = "PAYPAL-ORDER-" + System.currentTimeMillis();
            String transactionId = "PP-" + System.currentTimeMillis();
            double fee = (amount * payPalConfig.getFeePercentage() / 100) + payPalConfig.getFixedFee();
            
            System.out.println("PayPal Order ID: " + orderId);
            System.out.println("PayPal Transaction ID: " + transactionId);
            System.out.println("PayPal Fee: $" + String.format("%.2f", fee));
            System.out.println("Net Amount: $" + String.format("%.2f", amount - fee));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("PayPal API call interrupted", e);
        }
    }

    private String maskClientId(String clientId) {
        if (clientId == null || clientId.length() < 8) {
            return "****";
        }
        return clientId.substring(0, 4) + "****" + clientId.substring(clientId.length() - 4);
    }
}
