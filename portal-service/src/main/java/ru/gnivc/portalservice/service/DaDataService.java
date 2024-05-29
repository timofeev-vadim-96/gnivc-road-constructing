package ru.gnivc.portalservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.gnivc.portalservice.config.DadataProperties;
import ru.gnivc.portalservice.model.CompanyEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class DaDataService {
    private final WebClient webClient;

    public DaDataService(DadataProperties dadataProperties) {
        this.webClient = WebClient.builder()
                .baseUrl(dadataProperties.apiEndpoint)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Token " + dadataProperties.apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-secret", dadataProperties.secret)
                .build();
    }

    public Mono<JsonNode> getCompanyDetailsByINN(String inn) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("query", inn); // Запрос на поиск адресов

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public CompanyEntity serializeResponseToCompany(Mono<JsonNode> response) {
        return response.flatMap(resp -> {
            String address = resp.path("suggestions").get(0)
                    .path("data")
                    .path("address")
                    .path("value").asText();
            String kpp = resp.path("suggestions").get(0)
                    .path("data")
                    .path("kpp").asText();
            String ogrn = resp.path("suggestions").get(0)
                    .path("data")
                    .path("ogrn").asText();
            String inn = resp.path("suggestions").get(0)
                    .path("data")
                    .path("inn").asText();
            String fullName = resp.path("suggestions").get(0)
                    .path("data")
                    .path("name")
                    .path("full").asText();

            return Mono.just(CompanyEntity.builder()
                    .inn(inn)
                    .ogrn(ogrn)
                    .kpp(kpp)
                    .name(fullName)
                    .address(address)
                    .build());
        }).block();
    }
}
