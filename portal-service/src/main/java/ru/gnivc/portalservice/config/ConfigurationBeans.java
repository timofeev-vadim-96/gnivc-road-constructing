package ru.gnivc.portalservice.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConfigurationBeans {
    private final KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.serverUrl)
                .realm(keycloakProperties.realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(keycloakProperties.resource)
                .clientSecret(keycloakProperties.clientKeyPassword)
                .username(keycloakProperties.adminUsername)
                .password(keycloakProperties.adminPassword)
                .build();
    }
}
