package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.TaskEntity;
import ru.gnivc.logistservice.model.TripEntity;

import java.util.List;

@Repository
public interface TripDao extends JpaRepository<TripEntity, Long> {
    List<TripEntity> getAllByTask(TaskEntity task);
}
