package com.gamma.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "gamma.ms.rest.config")
@Component
public class RestServicesConfig {
    private Map<String, String> email;

    public RestServicesConfig() {
    }

    public Map<String, String> getEmail() {
        return email;
    }

    public void setEmail(Map<String, String> email) {
        this.email = email;
    }
}
