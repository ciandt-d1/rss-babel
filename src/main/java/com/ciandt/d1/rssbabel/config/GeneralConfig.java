package com.ciandt.d1.rssbabel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * General configuration
 */
@Configuration
@ConfigurationProperties(prefix = "general")
public class GeneralConfig {

    private String environment;
    private String localEndpoint;
    private String memcachedServers;

    public GeneralConfig() {
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLocalEndpoint() {
        return localEndpoint;
    }

    public void setLocalEndpoint(String localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public String getMemcachedServers() {
        return memcachedServers;
    }

    public void setMemcachedServers(String memcachedServers) {
        this.memcachedServers = memcachedServers;
    }
}
