package ru.gnivc.logistservice.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gnivc.logistservice.model.TaskEntity;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TripDto {
    private Long id;

    private TaskEntity task;

    private LocalDateTime creationTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String currentStatus;
}
