package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.portalservice.model.VehicleEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.List;

@Repository
@AfterReturningLogger
public interface VehicleDao extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findAllByCompanyId(long id);
}
