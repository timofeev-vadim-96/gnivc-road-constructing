package ru.gnivc.logistservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.logistservice.dto.output.TripDto;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;

import java.util.List;

public interface TripService {
    ResponseEntity<TripEntity> create(long taskId, String companyName);
    ResponseEntity<TripDto> get(long tripId, String companyName);
    ResponseEntity<List<TripEntity>> getAllByTask(long taskId, String companyName);
    ResponseEntity<Void> removeById(long tripId, String companyName);
    ResponseEntity<List<TripEventEntity>> getTripWithEvents(long tripId, String companyName);
    ResponseEntity<List<TripLocationEntity>> getTripWithLocaitonPoints(long tripId, String companyName);
}
