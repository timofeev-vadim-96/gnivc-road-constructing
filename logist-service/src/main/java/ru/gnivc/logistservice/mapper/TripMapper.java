package ru.gnivc.logistservice.mapper;

import ru.gnivc.logistservice.dto.output.TripDto;
import ru.gnivc.logistservice.dto.output.TripWithEventsDto;
import ru.gnivc.logistservice.dto.output.TripWithLocationPointsDto;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;

import java.util.List;

public class TripMapper {

    public static TripDto convertTripToDto(
            TripEntity trip,
            TripEventEntity lastEvent) {
        return TripDto.builder()
                .id(trip.getId())
                .task(trip.getTask())
                .creationTime(trip.getCreationTime())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .currentStatus(lastEvent.getEvent())
                .build();
    }

    public static TripWithEventsDto convertTripToDtoWithEvents(TripEntity trip,
                                                                TripEventEntity lastEvent,
                                                                List<TripEventEntity> events) {
        return TripWithEventsDto.builder()
                .id(trip.getId())
                .task(trip.getTask())
                .creationTime(trip.getCreationTime())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .currentStatus(lastEvent.getEvent())
                .tripEvents(events)
                .build();
    }

    public static TripWithLocationPointsDto convertTripToDtoWithLocationPoints(TripEntity trip,
                                                                                TripEventEntity lastEvent,
                                                                                List<TripLocationEntity> locationPoints) {
        return TripWithLocationPointsDto.builder()
                .id(trip.getId())
                .task(trip.getTask())
                .creationTime(trip.getCreationTime())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .currentStatus(lastEvent.getEvent())
                .locationPoints(locationPoints)
                .build();
    }
}
