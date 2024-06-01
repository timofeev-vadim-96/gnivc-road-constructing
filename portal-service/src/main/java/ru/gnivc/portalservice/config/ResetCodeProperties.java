package ru.gnivc.portalservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "resetcode")
@Setter
@Getter
public class ResetCodeProperties {
    public int length;
    public int lowerCaseCharsQuantity;
    public int upperCaseCharsQuantity;
    public int digitsQuantity;
    public int expirationDateInMinutes;
}

