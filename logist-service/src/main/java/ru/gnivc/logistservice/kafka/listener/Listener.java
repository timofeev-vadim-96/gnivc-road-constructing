package ru.gnivc.logistservice.kafka.listener;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gnivc.logistservice.dao.TripDao;
import ru.gnivc.logistservice.dao.TripEventDao;
import ru.gnivc.logistservice.dao.TripLocationDao;
import ru.gnivc.logistservice.dto.input.TripEventDto;
import ru.gnivc.logistservice.dto.input.TripLocationDto;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.util.Optional;

/**
 * Слушатель типа Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Listener {
    private final TripDao tripDao;
    private final TripEventDao tripEventDao;
    private final TripLocationDao tripLocationDao;

    @KafkaListener(
            topics = "trip_car_location",
            containerFactory = "tripLocationListenerContainerFactory")
    void listen(ConsumerRecord<String, TripLocationDto> record) {
        TripLocationDto locationPoint = record.value();
        log.info("Received trip location from Kafka with location point: {}", locationPoint.getLocation());

        Optional<TripEntity> tripOptional = tripDao.findById(locationPoint.getTripId());
        if (tripOptional.isEmpty()) {
            throw new NotFoundException("Trip with id = " + locationPoint.getTripId() + " received from Kafka not found.");
        } else {
            TripLocationEntity tripLocationEntity = TripLocationEntity.builder()
                    .location(locationPoint.getLocation())
                    .time(locationPoint.getTime())
                    .trip(tripOptional.get())
                    .build();
            tripLocationDao.save(tripLocationEntity);
        }
    }

    @KafkaListener(
            topics = "trip_event",
            containerFactory = "tripEventListenerContainerFactory")
    @Transactional
    void listenTripEvent(ConsumerRecord<String, @Valid TripEventDto> record) {
        TripEventDto event = record.value();
        log.info("Received trip event from Kafka: {}", event.getTripEventEnum());

        Optional<TripEntity> tripOptional = tripDao.findById(event.getTripId());
        if (tripOptional.isEmpty()) {
            throw new NotFoundException("Trip with id = " + event.getTripId() + " received from Kafka not found.");
        } else {
            TripEntity trip = tripOptional.get();
            boolean isUpdated = false;
            if (event.getTripEventEnum().equals(TripEventEnum.TRIP_STARTED)) {
                trip.setStartTime(event.getTime());
                isUpdated = true;
            } else if (event.getTripEventEnum().equals(TripEventEnum.TRIP_ENDED)) {
                trip.setEndTime(event.getTime());
                isUpdated = true;
            }
            TripEventEntity tripEventEntity = TripEventEntity.builder()
                    .event(event.getTripEventEnum())
                    .time(event.getTime())
                    .trip(tripOptional.get())
                    .build();
            if (isUpdated) {
                tripDao.save(trip);
            }
            tripEventDao.save(tripEventEntity);
        }
    }
}
