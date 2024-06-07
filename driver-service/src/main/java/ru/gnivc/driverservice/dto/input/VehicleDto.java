package ru.gnivc.driverservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class VehicleDto {
    private Long id;
    private String stateNumber;
}
