package com.codewithmosh.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Credit Card payment configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "creditCard")
public class CreditCardConfig {
    
    private boolean enabled = true;
    private int timeout = 1500;

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
} 