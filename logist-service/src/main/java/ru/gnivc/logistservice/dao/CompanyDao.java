package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.CompanyEntity;

import java.util.Optional;

@Repository
public interface CompanyDao extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByCompanyName(String companyName);
}
