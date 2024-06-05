package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.VehicleEntity;

@Repository
public interface VehicleDao extends JpaRepository<VehicleEntity, Long> {
}
