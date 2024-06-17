package ru.gnivc.dwhservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.dwhservice.model.CompanyEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.Optional;

@Repository
@AfterReturningLogger
public interface CompanyDao extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByName(String name);
}
