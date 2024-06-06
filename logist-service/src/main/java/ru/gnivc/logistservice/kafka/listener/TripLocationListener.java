package ru.gnivc.logistservice.kafka.listener;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.gnivc.logistservice.dao.TripDao;
import ru.gnivc.logistservice.dao.TripLocationDao;
import ru.gnivc.logistservice.dto.input.TripLocationDto;
import ru.gnivc.logistservice.mapper.JsonMapper;
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
    private final JsonMapper jsonMapper;
    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    @KafkaListener(
            topics = "trip_car_location",
            containerFactory = "tripLocationListenerContainerFactory",
            groupId = "trip_location_group")
    void listen(ConsumerRecord<String, String> record) {
        String data = record.value();
        log.info("Received trip location from Kafka with location point: {}", data);

        TripLocationDto locationPoint;
        try {
            locationPoint = jsonMapper.getTripLocationDto(data);
        } catch (Exception e) {
            throw new RuntimeException("Exception when trying to parse JSON massage from Kafka.", e);
        }

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
}
