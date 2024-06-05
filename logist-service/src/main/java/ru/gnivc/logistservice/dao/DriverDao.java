package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.DriverEntity;

@Repository
public interface DriverDao extends JpaRepository<DriverEntity, Long> {
}
