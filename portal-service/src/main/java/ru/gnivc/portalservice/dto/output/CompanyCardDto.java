package ru.gnivc.portalservice.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCardDto {
    private Long id;
    private String name;
    private String inn;
    private String address;
    private String kpp;
    private String ogrn;
    private int logistsQuantity;
    private int driversQuantity;
}
