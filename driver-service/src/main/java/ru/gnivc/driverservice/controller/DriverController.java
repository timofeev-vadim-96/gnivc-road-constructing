package ru.gnivc.driverservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.driverservice.dto.input.TaskDto;
import ru.gnivc.driverservice.dto.input.TripDto;
import ru.gnivc.driverservice.dto.output.TripEventDto;
import ru.gnivc.driverservice.dto.output.TripLocationDto;
import ru.gnivc.driverservice.service.DriverService;
import ru.gnivc.driverservice.service.KafkaProducer;

import java.util.List;

@RestController
@RequestMapping("driver/v1")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/task/{driverId}")
    public ResponseEntity<List<TaskDto>> getDriverTasks(@RequestParam String companyName,
                                                        @PathVariable long driverId) {
        return driverService.getDriversTasks(driverId, companyName);
    }

    @PostMapping("/trip")
    public ResponseEntity<TripDto> create(@RequestParam long taskId,
                                          @RequestParam String companyName) {
        return driverService.createTrip(taskId, companyName);
    }

    @PostMapping("/location")
    public ResponseEntity<Void> postLocationPoint(@RequestParam String companyName,
                                                  @Valid @RequestBody TripLocationDto locationPoint) {
        return kafkaProducer.sendLocationPoint(locationPoint);
    }

    @PostMapping("/event")
    public ResponseEntity<Void> postTripEvent(@RequestParam String companyName,
                                              @Valid @RequestBody TripEventDto event) {
        return kafkaProducer.sendTripEvent(event);
    }
}
