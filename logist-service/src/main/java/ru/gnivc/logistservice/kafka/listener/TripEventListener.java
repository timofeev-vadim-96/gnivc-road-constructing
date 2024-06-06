package ru.gnivc.logistservice.kafka.listener;

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
import ru.gnivc.logistservice.mapper.JsonMapper;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.util.Optional;

/**
 * Слушатель типа Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TripEventListener {
    private final TripDao tripDao;
    private final TripEventDao tripEventDao;
    private final JsonMapper jsonMapper;

    @KafkaListener(
            topics = "trip_event",
            containerFactory = "tripEventListenerContainerFactory",
            groupId = "trip_event_group")
    @Transactional
    void listenTripEvent(ConsumerRecord<String, String> record) {
        String data = record.value();
        log.info("Received trip event from Kafka: {}", data);

        TripEventDto event;
        try {
            event = jsonMapper.getTripEventDto(data);
        } catch (Exception e){
            throw new RuntimeException("Exception when trying to parse JSON message from Kafka.", e);
        }

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
                    .event(event.getTripEventEnum().name())
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
