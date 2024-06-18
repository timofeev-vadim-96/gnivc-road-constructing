package ru.gnivc.portalservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import ru.gnivc.portalservice.model.CompanyEntity;

import java.util.Optional;

public interface DaDataService {
    JsonNode getCompanyDetailsByINN(String inn);

    Optional<CompanyEntity> serializeResponseToCompany(JsonNode response);
}
