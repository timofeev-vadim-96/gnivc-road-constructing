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
                .baseUrl(dadataProperties.getApiEndpoint())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Token " + dadataProperties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-secret", dadataProperties.getSecret())
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
            String address = getAddress(response);
            String kpp = getKpp(response);
            String ogrn = getOgrn(response);
            String inn = getInn(response);
            String fullName = getFullName(response);

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

    private String getFullName(JsonNode response) {
        String fullName = response.path("suggestions").get(0)
                .path("data")
                .path("name")
                .path("full").asText();
        return fullName;
    }

    private String getInn(JsonNode response) {
        String inn = response.path("suggestions").get(0)
                .path("data")
                .path("inn").asText();
        return inn;
    }

    private String getOgrn(JsonNode response) {
        String ogrn = response.path("suggestions").get(0)
                .path("data")
                .path("ogrn").asText();
        return ogrn;
    }

    private String getKpp(JsonNode response) {
        String kpp = response.path("suggestions").get(0)
                .path("data")
                .path("kpp").asText();
        return kpp;
    }

    private String getAddress(JsonNode response) {
        String address = response.path("suggestions").get(0)
                .path("data")
                .path("address")
                .path("value").asText();
        return address;
    }
}
