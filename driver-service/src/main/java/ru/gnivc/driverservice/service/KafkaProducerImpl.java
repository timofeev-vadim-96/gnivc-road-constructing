package ru.gnivc.driverservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.driverservice.dto.output.TripEventDto;
import ru.gnivc.driverservice.dto.output.TripLocationDto;
import ru.gnivc.driverservice.mapper.JsonConverter;

@Service
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String LOCATION_POINT_TOPIC = "trip_car_location";
    private final String EVENT_TOPIC = "trip_event";
    private final JsonConverter jsonConverter;

    public ResponseEntity<Void> sendLocationPoint(TripLocationDto locationPoint) {
        try {
            String message = jsonConverter.serializeToJson(locationPoint);
            SendResult<String, String> result = kafkaTemplate.send(LOCATION_POINT_TOPIC, message).get();
            if (result.getRecordMetadata() == null) {
                String answer = "The trip location point was not sent successfully";
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, answer);
            } else {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Void> sendTripEvent(TripEventDto event) {
        try {
            String message = jsonConverter.serializeToJson(event);
            SendResult<String, String> result = kafkaTemplate.send(EVENT_TOPIC, message).get();
            if (result.getRecordMetadata() == null) {
                String answer = "The trip event was not sent successfully";
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, answer);
            } else {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
