package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.VehicleEntity;
import xxx.yyy.annotation.AfterReturningLogger;

@Repository
@AfterReturningLogger
public interface VehicleDao extends JpaRepository<VehicleEntity, Long> {
}
