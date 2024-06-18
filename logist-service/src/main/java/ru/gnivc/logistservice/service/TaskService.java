package ru.gnivc.logistservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.logistservice.dto.input.TaskDto;
import ru.gnivc.logistservice.model.TaskEntity;

import java.util.List;

public interface TaskService {
    ResponseEntity<Void> removeById(long taskId, String companyName);

    ResponseEntity<List<TaskEntity>> findAllByCompanyName(String companyName);

    ResponseEntity<TaskEntity> findById(long taskId, String companyName);

    ResponseEntity<TaskEntity> create(TaskDto taskDto, String companyName);

    ResponseEntity<List<TaskEntity>> getTaskByDriver(long driverId, String companyName);
}
