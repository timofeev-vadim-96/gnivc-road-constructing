package ru.gnivc.dwhservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gnivc.dwhservice.model.CompanyEntity;
import ru.gnivc.dwhservice.model.CompanyStatisticsEntity;

import java.util.Optional;

public interface CompanyStatisticsDao extends JpaRepository<CompanyStatisticsEntity, Long> {

    Optional<CompanyStatisticsEntity> findByCompany(CompanyEntity company);
}
