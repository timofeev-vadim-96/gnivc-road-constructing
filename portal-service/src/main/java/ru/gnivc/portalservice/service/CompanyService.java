package ru.gnivc.portalservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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
@RequiredArgsConstructor
public class CompanyService {
    private final KeycloakService keycloakService;
    private final DaDataService daDataService;

    private final CompanyDao companyDao;
    private final CustomQueriesDao queriesDao;
    private final CompanyUserService companyUserService;

    public CompanyEntity createCompany(String inn, String email) {
        Optional<UserRepresentation> user = keycloakService.findUserByMail(email);
        if (user.isEmpty()) return null;
        else {
            String userId = user.get().getId();
            Mono<JsonNode> companyDetails = daDataService.getCompanyDetailsByINN(inn);
            CompanyEntity company = daDataService.serializeResponseToCompany(companyDetails);
            keycloakService.createClient(company.getName());
            keycloakService.assignClientLevelRoleToUser(userId, company.getName(), ClientRole.ROLE_ADMIN);

            companyUserService.bindUserWithCompany(email, company.getName(), ClientRole.ROLE_ADMIN);

            return companyDao.save(company);
        }
    }

    public List<String> getClientsRoles() {
        return Arrays.stream(ClientRole.values()).map(Enum::name).toList();
    }

    public List<SimpleCompanyDto> getCompanies() {
        return queriesDao.getCompanies();
    }


    public CompanyCardDto getCompanyCard(String companyId) {
        Optional<Integer> logists = queriesDao.countCompanyUsersWithSpecificRole(companyId, ClientRole.ROLE_LOGIST);
        Optional<Integer> drivers = queriesDao.countCompanyUsersWithSpecificRole(companyId, ClientRole.ROLE_DRIVER);
        Optional<CompanyEntity> companyOptional = companyDao.findByName(companyId);
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
