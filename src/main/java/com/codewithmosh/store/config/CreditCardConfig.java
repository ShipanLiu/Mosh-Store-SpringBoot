package com.codewithmosh.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Credit Card payment configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "creditCard")
public class CreditCardConfig {
    
    private boolean enabled = true;
    private int timeout = 1500;
    private String processorName = "Generic Credit Card Processor";
    private List<String> acceptedCardTypes = new ArrayList<>();
    private double processingFeePercentage = 2.5; // 2.5% processing fee
    private boolean requireCvv = true;
    private boolean requireBillingAddress = false;
    private int maxRetryAttempts = 3;

    // Initialize default accepted card types
    public CreditCardConfig() {
        acceptedCardTypes.add("VISA");
        acceptedCardTypes.add("MASTERCARD");
        acceptedCardTypes.add("AMERICAN_EXPRESS");
        acceptedCardTypes.add("DISCOVER");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public List<String> getAcceptedCardTypes() {
        return acceptedCardTypes;
    }

    public void setAcceptedCardTypes(List<String> acceptedCardTypes) {
        this.acceptedCardTypes = acceptedCardTypes;
    }

    public double getProcessingFeePercentage() {
        return processingFeePercentage;
    }

    public void setProcessingFeePercentage(double processingFeePercentage) {
        this.processingFeePercentage = processingFeePercentage;
    }

    public boolean isRequireCvv() {
        return requireCvv;
    }

    public void setRequireCvv(boolean requireCvv) {
        this.requireCvv = requireCvv;
    }

    public boolean isRequireBillingAddress() {
        return requireBillingAddress;
    }

    public void setRequireBillingAddress(boolean requireBillingAddress) {
        this.requireBillingAddress = requireBillingAddress;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }
} 