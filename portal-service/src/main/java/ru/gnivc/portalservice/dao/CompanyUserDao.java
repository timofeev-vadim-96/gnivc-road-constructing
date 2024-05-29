package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gnivc.portalservice.model.CompanyUserUtilEntity;

public interface CompanyUserDao extends JpaRepository<CompanyUserUtilEntity, Long> {
}
