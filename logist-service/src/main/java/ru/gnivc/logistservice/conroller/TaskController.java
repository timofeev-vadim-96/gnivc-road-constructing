package ru.gnivc.logistservice.conroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.logistservice.dto.input.TaskDto;
import ru.gnivc.logistservice.model.TaskEntity;
import ru.gnivc.logistservice.service.TaskService;
import ru.gnivc.logistservice.service.TaskServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logist/v1/task")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(@RequestParam String companyName,
                                                 @Valid @RequestBody TaskDto task){
        return taskService.create(task, companyName);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskEntity> getTask(@RequestParam String companyName,
                                              @PathVariable long taskId){
        return taskService.findById(taskId, companyName);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTask(@RequestParam String companyName,
                                           @PathVariable long taskId){
        return taskService.removeById(taskId, companyName);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TaskEntity>> getAllByCompany(@RequestParam String companyName){
        return taskService.findAllByCompanyName(companyName);
    }

    @GetMapping("/byDriver/{driverId}")
    public ResponseEntity<List<TaskEntity>> getAllByDriver(@PathVariable long driverId,
                                                           @RequestParam String companyName){
        return taskService.getTaskByDriver(driverId, companyName);
    }

}
