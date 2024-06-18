package ru.gnivc.logistservice.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Dto from portal-ms
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehicleDto {
    private Long id;

    private String vin;

    private LocalDate releaseYear;

    private String stateNumber;

    private CompanyEntity company;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CompanyEntity {
        private Long id;

        private String name;

        private String inn;

        private String address;

        private String kpp;

        private String ogrn;
    }
}
