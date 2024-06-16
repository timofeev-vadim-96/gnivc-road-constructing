package ru.gnivc.dwhservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.dwhservice.model.CompanyEntity;
import ru.gnivc.dwhservice.model.CompanyStatisticsEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.Optional;

@Repository
@AfterReturningLogger
public interface CompanyStatisticsDao extends JpaRepository<CompanyStatisticsEntity, Long> {

    Optional<CompanyStatisticsEntity> findByCompany(CompanyEntity company);
}
