package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gnivc.portalservice.model.VehicleEntity;

public interface VehicleDao extends JpaRepository<VehicleEntity, Long> {
}
