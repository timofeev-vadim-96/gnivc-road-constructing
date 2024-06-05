package ru.gnivc.portalservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gnivc.portalservice.model.VehicleEntity;

import java.util.List;

public interface VehicleDao extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findAllByCompanyId(long id);
}
