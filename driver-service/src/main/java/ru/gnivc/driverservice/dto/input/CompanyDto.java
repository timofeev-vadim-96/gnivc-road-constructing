package ru.gnivc.driverservice.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CompanyDto {
    private Long id;

    private String companyName;
}
