package com.codewithmosh.store.service.payment.processors;

import com.codewithmosh.store.config.CreditCardConfig;
import org.springframework.stereotype.Service;

@Service("creditCard")
public class CreditCardPaymentService implements PaymentService {

    private final CreditCardConfig creditCardConfig;

    public CreditCardPaymentService(CreditCardConfig creditCardConfig) {
        this.creditCardConfig = creditCardConfig;
    }

    @Override
    public void processPayment(double amount) {
        if (!creditCardConfig.isEnabled()) {
            throw new IllegalStateException("Credit Card payment service is disabled");
        }

        System.out.println("=== Credit Card Payment Processing ===");
        System.out.println("Processing credit card payment of $" + amount);
        System.out.println("Processor: " + creditCardConfig.getProcessorName());
        System.out.println("Payment Timeout: " + creditCardConfig.getTimeout() + "ms");
        System.out.println("Credit Card Service Status: " + (creditCardConfig.isEnabled() ? "ACTIVE" : "DISABLED"));
        System.out.println("Accepted Card Types: " + creditCardConfig.getAcceptedCardTypes());
        System.out.println("CVV Required: " + creditCardConfig.isRequireCvv());
        System.out.println("Billing Address Required: " + creditCardConfig.isRequireBillingAddress());
        
        // Simulate credit card processing
        simulateCreditCardProcessing(amount);
        
        System.out.println("Credit card payment completed successfully!");
        System.out.println("========================================");
    }

    private void simulateCreditCardProcessing(double amount) {
        try {
            System.out.println("Validating credit card details...");
            
            if (creditCardConfig.isRequireCvv()) {
                System.out.println("Verifying CVV...");
                Thread.sleep(30);
            }
            
            if (creditCardConfig.isRequireBillingAddress()) {
                System.out.println("Validating billing address...");
                Thread.sleep(40);
            }
            
            Thread.sleep(50);
            
            System.out.println("Connecting to payment processor...");
            Thread.sleep(80);
            
            System.out.println("Authorizing transaction...");
            Thread.sleep(120);
            
            // Generate realistic transaction details
            String authCode = "AUTH-" + (int)(Math.random() * 1000000);
            String transactionId = "TXN-" + System.currentTimeMillis();
            String cardType = creditCardConfig.getAcceptedCardTypes().get(
                (int)(Math.random() * creditCardConfig.getAcceptedCardTypes().size())
            );
            double processingFee = amount * creditCardConfig.getProcessingFeePercentage() / 100;
            
            System.out.println("Card Type: " + cardType);
            System.out.println("Authorization Code: " + authCode);
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Processing Fee: $" + String.format("%.2f", processingFee));
            System.out.println("Net Amount: $" + String.format("%.2f", amount - processingFee));
            System.out.println("Max Retry Attempts: " + creditCardConfig.getMaxRetryAttempts());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Credit card processing interrupted", e);
        }
    }
} 