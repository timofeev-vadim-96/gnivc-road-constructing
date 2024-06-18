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
public class CompanyServiceImpl implements CompanyService {
    private final KeycloakService keycloakService;

    private final DaDataService daDataService;

    private final CompanyDao companyDao;

    private final CustomQueriesDao queriesDao;

    private final CompanyUserBinder companyUserService;

    @Transactional
    public ResponseEntity<CompanyEntity> createCompany(String inn, String email) {
        Optional<UserRepresentation> user = keycloakService.findUserByMail(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with email: %s was not found", email));
        } else {
            String userId = user.get().getId();
            Optional<CompanyEntity> company = getCompanyFromDaData(inn);

            CompanyEntity saved = companyDao.save(company.get());
            companyUserService.bindUserWithCompany(email, company.get().getName(), ClientRole.ROLE_ADMIN);

            int status = keycloakService.createClient(company.get().getName());
            if (status == 201) {
                keycloakService.assignClientLevelRoleToUser(userId, company.get().getName(), ClientRole.ROLE_ADMIN);

                return new ResponseEntity<>(saved, HttpStatus.CREATED);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Company with inn: %s was already exists.", inn));
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
        if (companyOptional.isEmpty() || drivers.isEmpty() || logists.isEmpty()) {
            return null;
        } else {
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

    @Transactional
    public ResponseEntity<Void> remove(String companyName) {
        Optional<CompanyEntity> company = companyDao.findByName(companyName);
        if (company.isEmpty()) {
            String answer = String.format("Company with id = %s not found", companyName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else {
            companyDao.delete(company.get());
            keycloakService.removeClient(companyName);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<CompanyEntity> updateCompany(String inn, String companyName) {
        Optional<CompanyEntity> companyFromDaDataOptional = getCompanyFromDaData(inn);
        Optional<CompanyEntity> companyOptional = companyDao.findByName(companyName);
        if (companyOptional.isEmpty() || companyFromDaDataOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Company with inn: %s not found.", inn));
        } else {
            CompanyEntity company = getCompanyEntity(companyFromDaDataOptional.get(), companyOptional.get());

            CompanyEntity saved = companyDao.save(company);
            keycloakService.updateClientName(saved.getName());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
    }

    private static CompanyEntity getCompanyEntity(CompanyEntity companyFromDaData, CompanyEntity company) {
        company.setName(companyFromDaData.getName());
        company.setOgrn(companyFromDaData.getOgrn());
        company.setKpp(companyFromDaData.getKpp());
        company.setAddress(companyFromDaData.getAddress());
        return company;
    }

    private Optional<CompanyEntity> getCompanyFromDaData(String inn) {
        JsonNode companyDetails = daDataService.getCompanyDetailsByINN(inn);
        log.info("Company details from DaData: {}", companyDetails);

        Optional<CompanyEntity> company = daDataService.serializeResponseToCompany(companyDetails);
        if (company.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Company with inn: %s not found.", inn));
        }
        return company;
    }
}
