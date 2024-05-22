package ru.gnivc.portalservice.config;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Slf4j
@Setter
public class KeycloakProperties {
    public String authServerUrl;
    public String realm;
    public String resource;
    public String clientKeyPassword;
}
