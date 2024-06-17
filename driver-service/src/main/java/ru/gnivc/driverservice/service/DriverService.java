package ru.gnivc.driverservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.driverservice.dto.input.TaskDto;
import ru.gnivc.driverservice.dto.input.TripDto;

import java.util.List;

public interface DriverService {
    ResponseEntity<List<TaskDto>> getDriversTasks(long driverId, String companyName);

    ResponseEntity<TripDto> createTrip(long tripId, String companyName);
}
