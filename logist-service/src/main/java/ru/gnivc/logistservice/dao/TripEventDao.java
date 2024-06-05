package ru.gnivc.logistservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;

import java.util.List;

@Repository
public interface TripEventDao extends JpaRepository<TripEventEntity, Long> {
    List<TripEventEntity> findAllByTrip(TripEntity trip);
}
