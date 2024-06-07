package ru.gnivc.driverservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.driverservice.dto.input.TaskDto;
import ru.gnivc.driverservice.dto.input.TripDto;
import ru.gnivc.driverservice.dto.output.TripLocationDto;
import ru.gnivc.driverservice.provider.LogistProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final LogistProvider logistProvider;

    public ResponseEntity<List<TaskDto>> getDriversTasks(long driverId, String companyName){
        List<TaskDto> tasksByDriverId = logistProvider.getTasksByDriverId(driverId, companyName);
        if (tasksByDriverId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(tasksByDriverId, HttpStatus.OK);
        }
    }

    public ResponseEntity<TripDto> createTrip(long tripId, String companyName){
        TripDto trip = logistProvider.createTrip(tripId, companyName);
        if (trip == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(trip, HttpStatus.CREATED);
        }
    }
}
