package ru.gnivc.logistservice.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gnivc.logistservice.model.TaskEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TripWithEventsDto {
    private Long id;
    private TaskEntity task;
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TripEventEnum currentStatus;
    private List<TripEventEntity> tripEvents;
}
