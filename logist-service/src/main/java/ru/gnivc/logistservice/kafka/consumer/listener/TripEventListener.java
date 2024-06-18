package ru.gnivc.logistservice.kafka.consumer.listener;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gnivc.logistservice.dao.TripDao;
import ru.gnivc.logistservice.dao.TripEventDao;
import ru.gnivc.logistservice.dto.input.TripEventDto;
import ru.gnivc.logistservice.mapper.JsonConverter;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.util.Optional;

/**
 * Kafka Listener
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TripEventListener {
    private final TripDao tripDao;

    private final TripEventDao tripEventDao;

    private final JsonConverter jsonConverter;

    @KafkaListener(
            topics = "trip_event",
            containerFactory = "tripEventListenerContainerFactory",
            groupId = "trip_event_group")
    @Transactional
    void listenTripEvent(ConsumerRecord<String, String> record) {
        String data = record.value();
        log.info("Received trip event from Kafka: {}", data);

        TripEventDto event = parseJsonToTripEventDto(data);

        Optional<TripEntity> tripOptional = tripDao.findById(event.getTripId());
        if (tripOptional.isEmpty()) {
            throw new NotFoundException("Trip with id = " + event.getTripId() + " received from Kafka not found.");
        } else {
            TripEntity trip = tripOptional.get();

            if (isTripEventUnique(event.getTripEventEnum(), trip)) {
                throw new DuplicateRequestException(
                        String.format("For the trip with id = %d the event %s has already happened.",
                                trip.getId(),
                                event.getTripEventEnum().name()));
            }

            updateTripEntity(event, trip);
            saveTripEvent(event, tripOptional.get());
        }
    }

    private void saveTripEvent(TripEventDto event, TripEntity tripOptional) {
        TripEventEntity tripEventEntity = TripEventEntity.builder()
                .event(event.getTripEventEnum().name())
                .time(event.getTime())
                .trip(tripOptional)
                .build();
        tripEventDao.save(tripEventEntity);
    }

    private void updateTripEntity(TripEventDto event, TripEntity trip) {
        boolean isUpdated = false;
        if (event.getTripEventEnum().equals(TripEventEnum.TRIP_STARTED)) {
            trip.setStartTime(event.getTime());
            isUpdated = true;
        } else if (event.getTripEventEnum().equals(TripEventEnum.TRIP_ENDED)) {
            trip.setEndTime(event.getTime());
            isUpdated = true;
        }
        if (isUpdated) {
            tripDao.save(trip);
        }
    }

    private TripEventDto parseJsonToTripEventDto(String data) {
        TripEventDto event;
        try {
            event = jsonConverter.getTripEventDto(data);
        } catch (Exception e) {
            throw new RuntimeException("Exception when trying to parse JSON message from Kafka.", e);
        }
        return event;
    }

    private boolean isTripEventUnique(TripEventEnum event, TripEntity trip) {
        return tripEventDao.findAllByTrip(trip)
                .stream()
                .anyMatch(tripEvent -> tripEvent.getEvent().equals(event.name()));
    }
}
