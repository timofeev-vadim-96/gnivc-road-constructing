package ru.gnivc.driverservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.geom.Point2D;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private Point2D.Double startPoint;
    private Point2D.Double finishPoint;
    private String cargoDescription;
    private DriverDto driver;
    private VehicleDto vehicle;
    private CompanyDto company;
}
