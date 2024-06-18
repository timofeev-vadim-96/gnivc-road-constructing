package ru.gnivc.logistservice.kafka.consumer.listener;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.gnivc.logistservice.dao.TripDao;
import ru.gnivc.logistservice.dao.TripLocationDao;
import ru.gnivc.logistservice.dto.input.TripLocationDto;
import ru.gnivc.logistservice.mapper.JsonConverter;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;

import java.util.Optional;

/**
 * Слушатель типа Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TripLocationListener {
    private final TripDao tripDao;

    private final TripLocationDao tripLocationDao;

    private final JsonConverter jsonConverter;

    @KafkaListener(
            topics = "trip_car_location",
            containerFactory = "tripLocationListenerContainerFactory",
            groupId = "trip_location_group")
    void listen(ConsumerRecord<String, String> record) {
        String data = record.value();
        log.info("Received trip location from Kafka with location point: {}", data);

        TripLocationDto locationPoint;
        try {
            locationPoint = jsonConverter.getTripLocationDto(data);
        } catch (Exception e) {
            throw new RuntimeException("Exception when trying to parse JSON message from Kafka.", e);
        }

        Optional<TripEntity> tripOptional = tripDao.findById(locationPoint.getTripId());
        if (tripOptional.isEmpty()) {
            String answer = "Trip with id = " + locationPoint.getTripId() + " received from Kafka not found.";
            throw new NotFoundException(answer);
        } else {
            saveTripLocationEntity(locationPoint, tripOptional.get());
        }
    }

    private void saveTripLocationEntity(TripLocationDto locationPoint, TripEntity tripOptional) {
        TripLocationEntity tripLocationEntity = TripLocationEntity.builder()
                .location(locationPoint.getLocation())
                .time(locationPoint.getTime())
                .trip(tripOptional)
                .build();
        tripLocationDao.save(tripLocationEntity);
    }
}
