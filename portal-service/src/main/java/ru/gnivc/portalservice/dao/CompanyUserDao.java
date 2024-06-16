package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.portalservice.model.CompanyUserUtilEntity;
import xxx.yyy.annotation.AfterReturningLogger;

@AfterReturningLogger
@Repository
public interface CompanyUserDao extends JpaRepository<CompanyUserUtilEntity, Long> {
}
