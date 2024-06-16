package ru.gnivc.portalservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.gnivc.portalservice.config.DadataProperties;
import ru.gnivc.portalservice.model.CompanyEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class DaDataServiceImpl implements DaDataService {
    private final WebClient webClient;

    public DaDataServiceImpl(DadataProperties dadataProperties) {
        this.webClient = WebClient.builder()
                .baseUrl(dadataProperties.apiEndpoint)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Token " + dadataProperties.apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-secret", dadataProperties.secret)
                .build();
    }

    public JsonNode getCompanyDetailsByINN(String inn) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("query", inn); // Запрос на поиск адресов

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public Optional<CompanyEntity> serializeResponseToCompany(JsonNode response) {
        AtomicReference<Optional<CompanyEntity>> company = new AtomicReference<>();
        if (response == null) {
            company.set(Optional.empty());
        } else {
            String address = response.path("suggestions").get(0)
                    .path("data")
                    .path("address")
                    .path("value").asText();
            String kpp = response.path("suggestions").get(0)
                    .path("data")
                    .path("kpp").asText();
            String ogrn = response.path("suggestions").get(0)
                    .path("data")
                    .path("ogrn").asText();
            String inn = response.path("suggestions").get(0)
                    .path("data")
                    .path("inn").asText();
            String fullName = response.path("suggestions").get(0)
                    .path("data")
                    .path("name")
                    .path("full").asText();

            company.set(Optional.ofNullable(CompanyEntity.builder()
                    .inn(inn)
                    .ogrn(ogrn)
                    .kpp(kpp)
                    .name(fullName)
                    .address(address)
                    .build()));
        }
        return company.get();
    }
}
