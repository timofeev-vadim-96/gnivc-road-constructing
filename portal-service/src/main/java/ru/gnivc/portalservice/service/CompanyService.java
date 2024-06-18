package ru.gnivc.portalservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.portalservice.dto.output.CompanyCardDto;
import ru.gnivc.portalservice.dto.output.SimpleCompanyDto;
import ru.gnivc.portalservice.model.CompanyEntity;

import java.util.List;

public interface CompanyService {
    ResponseEntity<CompanyEntity> createCompany(String inn, String email);

    List<String> getClientsRoles();

    List<SimpleCompanyDto> getCompanies();

    CompanyCardDto getCompanyCard(String companyName);

    ResponseEntity<Void> remove(String companyName);

    ResponseEntity<CompanyEntity> updateCompany(String inn, String companyName);
}
