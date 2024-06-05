package ru.gnivc.logistservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gnivc.logistservice.model.CompanyEntity;
import ru.gnivc.logistservice.model.TaskEntity;

import java.util.List;

@Repository
public interface TaskDao extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByCompany(CompanyEntity company);
}
