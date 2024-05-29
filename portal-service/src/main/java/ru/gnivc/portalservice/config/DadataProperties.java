package ru.gnivc.portalservice.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dadata")
@Setter
public class DadataProperties {
    public String apiKey;
    public String secret;
    public String apiEndpoint;
}
