package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;

import java.util.List;

public interface TripLocationDao extends JpaRepository<TripLocationEntity, Long> {
    List<TripLocationEntity> findAllByTrip(TripEntity trip);
}
