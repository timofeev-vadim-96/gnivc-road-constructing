package ru.gnivc.logistservice.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto from portal-ms
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverDto {
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private boolean isRegistrator;
}
