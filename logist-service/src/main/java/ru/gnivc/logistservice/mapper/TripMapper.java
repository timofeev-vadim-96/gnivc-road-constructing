package ru.gnivc.logistservice.mapper;

import ru.gnivc.logistservice.dto.output.TripDto;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;

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
}
