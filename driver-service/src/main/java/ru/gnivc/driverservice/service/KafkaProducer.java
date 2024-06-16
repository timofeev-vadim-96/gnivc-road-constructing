package ru.gnivc.driverservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.driverservice.dto.output.TripEventDto;
import ru.gnivc.driverservice.dto.output.TripLocationDto;

public interface KafkaProducer {
    ResponseEntity<Void> sendLocationPoint(TripLocationDto locationPoint);

    ResponseEntity<Void> sendTripEvent(TripEventDto event);
}
