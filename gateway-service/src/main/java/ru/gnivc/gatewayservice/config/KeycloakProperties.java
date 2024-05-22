package ru.gnivc.gatewayservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    public String authServerUri;
    public String authPath;
    public String realm;
    public String resource;
    public String secret;
}
