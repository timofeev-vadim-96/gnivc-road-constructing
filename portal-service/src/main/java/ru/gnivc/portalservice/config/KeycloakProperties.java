package ru.gnivc.portalservice.config;

import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Setter
public class KeycloakProperties {
    public String serverUrl;
    public String realm;
    public String resource;
    public String clientKeyPassword;
    public String adminUsername;
    public String adminPassword;
}
