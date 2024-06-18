package ru.gnivc.portalservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Setter
@Getter
public class KeycloakProperties {
    private String serverUrl;

    private String realm;

    private String resource;

    private String clientKeyPassword;

    private String adminUsername;

    private String adminPassword;
}
