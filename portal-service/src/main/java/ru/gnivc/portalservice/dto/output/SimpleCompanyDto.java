package ru.gnivc.portalservice.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCompanyDto {
    private Long id;

    private String name;

    private String inn;
}
