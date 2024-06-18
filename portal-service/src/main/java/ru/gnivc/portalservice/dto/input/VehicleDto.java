package ru.gnivc.portalservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    @NotNull
    private String vin;

    @NotNull
    private LocalDate releaseYear;

    @NotNull
    private String stateNumber;
}
