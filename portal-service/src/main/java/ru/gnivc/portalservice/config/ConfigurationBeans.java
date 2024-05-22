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
                .serverUrl(keycloakProperties.authServerUrl)
                .realm(keycloakProperties.realm)
                .clientId(keycloakProperties.resource)
                .clientSecret(keycloakProperties.clientKeyPassword)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
