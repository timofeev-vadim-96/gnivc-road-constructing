package ru.gnivc.portalservice.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gnivc.portalservice.dao.CompanyDao;
import ru.gnivc.portalservice.dao.CompanyUserDao;
import ru.gnivc.portalservice.dao.UserDao;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.model.CompanyUserUtilEntity;
import ru.gnivc.portalservice.model.UserEntity;
import ru.gnivc.portalservice.util.ClientRole;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyUserService {
    private final CompanyUserDao companyUserDao;
    private final UserDao userDao;
    private final CompanyDao companyDao;

    public void bindUserWithCompany(String email, String clientId, ClientRole role) {
        Optional<UserEntity> user = userDao.findByEmail(email);
        Optional<CompanyEntity> company = companyDao.findByName(clientId);
        if (user.isEmpty() || company.isEmpty()) {
            throw new NotFoundException(
                    "The client or user was not found during the attempt to link them in the service table");
        } else {
            CompanyUserUtilEntity companyUser = CompanyUserUtilEntity.builder()
                    .userId(user.get())
                    .companyId(company.get())
                    .userRole(role.name())
                    .build();
            companyUserDao.save(companyUser);
        }
    }
}
