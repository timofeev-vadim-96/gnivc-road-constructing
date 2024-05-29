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
    public int length;
    public int lowerCaseCharsQuantity;
    public int upperCaseCharsQuantity;
    public int digitsQuantity;
    public int resetCodeLength;
    public int resetCodeLowerCaseCharsQuantity;
    public int resetCodeUpperCaseCharsQuantity;
    public int resetCodeDigitsQuantity;
    public int resetCodeExpirationDateInMinutes;
}
