package ru.gnivc.driverservice.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class DriverDto {
    private Long id;

    private String firstName;

    private String lastName;
}
