package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.TaskEntity;
import ru.gnivc.logistservice.model.TripEntity;
import xxx.yyy.annotation.AfterReturningLogger;

import java.util.List;

@Repository
@AfterReturningLogger
public interface TripDao extends JpaRepository<TripEntity, Long> {
    List<TripEntity> getAllByTask(TaskEntity task);
}
