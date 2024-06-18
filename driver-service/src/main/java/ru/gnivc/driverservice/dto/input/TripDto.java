package ru.gnivc.driverservice.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class TripDto {
    private Long id;

    private TaskDto task;

    private LocalDateTime creationTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}

