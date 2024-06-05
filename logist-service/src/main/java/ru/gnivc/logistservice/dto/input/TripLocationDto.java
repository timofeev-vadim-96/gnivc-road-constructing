package ru.gnivc.logistservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripLocationDto {
    @NotNull
    private Long tripId;
    @NotNull
    private Point2D.Double location;
    @NotNull
    private LocalDateTime time;
}
