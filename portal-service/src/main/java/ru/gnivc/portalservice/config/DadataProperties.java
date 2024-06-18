package ru.gnivc.portalservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dadata")
@Setter
@Getter
public class DadataProperties {
    private String apiKey;

    private String secret;

    private String apiEndpoint;
}
