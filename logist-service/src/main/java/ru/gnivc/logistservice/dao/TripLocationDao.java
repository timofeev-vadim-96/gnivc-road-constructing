package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.List;

@Repository
@AfterReturningLogger
public interface TripLocationDao extends JpaRepository<TripLocationEntity, Long> {
    List<TripLocationEntity> findAllByTrip(TripEntity trip);
}
