package ru.gnivc.portalservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dao.CompanyDao;
import ru.gnivc.portalservice.dao.CustomQueriesDao;
import ru.gnivc.portalservice.dto.output.CompanyCardDto;
import ru.gnivc.portalservice.dto.output.SimpleCompanyDto;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.util.ClientRole;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {
    private final KeycloakService keycloakService;
    private final DaDataService daDataService;

    private final CompanyDao companyDao;
    private final CustomQueriesDao queriesDao;
    private final CompanyUserService companyUserService;

    @Transactional
    public ResponseEntity<CompanyEntity> createCompany(String inn, String email) {
        Optional<UserRepresentation> user = keycloakService.findUserByMail(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The user with email: %s was not found", email));
        } else {
            String userId = user.get().getId();
            JsonNode companyDetails = daDataService.getCompanyDetailsByINN(inn);
            log.info("company details from DaData: {}", companyDetails);

            Optional<CompanyEntity> company = daDataService.serializeResponseToCompany(companyDetails);
            if (company.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The company with inn: %s not found.", inn));

            CompanyEntity saved = companyDao.save(company.get());
            companyUserService.bindUserWithCompany(email, company.get().getName(), ClientRole.ROLE_ADMIN);

            int status = keycloakService.createClient(company.get().getName());
            if (status == 201) {
                keycloakService.assignClientLevelRoleToUser(userId, company.get().getName(), ClientRole.ROLE_ADMIN);

                return new ResponseEntity<>(saved, HttpStatus.CREATED);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("The company with inn: %s was already exists.", inn));
            }
        }
    }

    public List<String> getClientsRoles() {
        return Arrays.stream(ClientRole.values()).map(Enum::name).toList();
    }

    public List<SimpleCompanyDto> getCompanies() {
        return queriesDao.getCompanies();
    }


    public CompanyCardDto getCompanyCard(String companyName) {
        Optional<Integer> logists = queriesDao.countCompanyUsersWithSpecificRole(companyName, ClientRole.ROLE_LOGIST);
        Optional<Integer> drivers = queriesDao.countCompanyUsersWithSpecificRole(companyName, ClientRole.ROLE_DRIVER);
        Optional<CompanyEntity> companyOptional = companyDao.findByName(companyName);
        if (companyOptional.isEmpty() || drivers.isEmpty() || logists.isEmpty()) return null;
        else {
            CompanyEntity company = companyOptional.get();
            return new CompanyCardDto(
                    company.getId(),
                    company.getName(),
                    company.getInn(),
                    company.getAddress(),
                    company.getKpp(),
                    company.getOgrn(),
                    logists.get(),
                    drivers.get());
        }
    }
}
