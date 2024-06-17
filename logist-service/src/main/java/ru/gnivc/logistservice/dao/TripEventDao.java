package ru.gnivc.logistservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.List;

@Repository
@AfterReturningLogger
public interface TripEventDao extends JpaRepository<TripEventEntity, Long> {
    List<TripEventEntity> findAllByTrip(TripEntity trip);
}
