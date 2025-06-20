package com.codewithmosh.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * PayPal payment configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {
    
    private boolean enabled = true;
    private String apiUrl = "https://api.paypal.com";
    private int timeout = 2000;
    private String environment = "sandbox"; // sandbox or live
    private String clientId;
    private String clientSecret;
    private List<String> supportedCurrencies = new ArrayList<>();
    private double feePercentage = 2.9; // PayPal's standard fee
    private double fixedFee = 0.30; // PayPal's fixed fee per transaction

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public List<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public void setSupportedCurrencies(List<String> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    public double getFeePercentage() {
        return feePercentage;
    }

    public void setFeePercentage(double feePercentage) {
        this.feePercentage = feePercentage;
    }

    public double getFixedFee() {
        return fixedFee;
    }

    public void setFixedFee(double fixedFee) {
        this.fixedFee = fixedFee;
    }
} 