package ru.gnivc.logistservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.geom.Point2D;

/**
 * Dto from http-request
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    @NotNull
    private Point2D.Double startPoint;
    @NotNull
    private Point2D.Double finishPoint;
    @NotNull
    private String cargoDescription;
    @NotNull
    private Long driverId;
    @NotNull
    private Long vehicleId;
}

