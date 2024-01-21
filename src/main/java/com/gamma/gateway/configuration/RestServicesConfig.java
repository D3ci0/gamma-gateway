package com.gamma.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "gamma.ms.rest.config")
@Component
public class RestServicesConfig {
    private Map<String, String> email;
    private Map<String, String> signature;
    private Map<String, String> store;

    public RestServicesConfig() {
    }

    public Map<String, String> getEmail() {
        return email;
    }

    public void setEmail(Map<String, String> email) {
        this.email = email;
    }

    public Map<String, String> getSignature() {
        return signature;
    }

    public void setSignature(Map<String, String> signature) {
        this.signature = signature;
    }

    public Map<String, String> getStore() {
        return store;
    }

    public void setStore(Map<String, String> store) {
        this.store = store;
    }
}
