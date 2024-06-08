package ru.gnivc.dwhservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gnivc.dwhservice.model.CompanyEntity;

import java.util.Optional;

public interface CompanyDao extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByName(String name);
}
