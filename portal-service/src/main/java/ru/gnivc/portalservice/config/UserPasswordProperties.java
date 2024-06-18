package ru.gnivc.portalservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak.user.password")
@Setter
@Getter
public class UserPasswordProperties {
    private int length;

    private int lowerCaseCharsQuantity;

    private int upperCaseCharsQuantity;

    private int digitsQuantity;
}

