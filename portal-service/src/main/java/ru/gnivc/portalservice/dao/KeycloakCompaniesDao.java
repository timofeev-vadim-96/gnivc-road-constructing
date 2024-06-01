package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.portalservice.model.KeycloakCompany;

import java.util.Optional;

@Repository
public interface KeycloakCompaniesDao extends JpaRepository<KeycloakCompany, String> {
    Optional<KeycloakCompany> findByName(String name);
}
