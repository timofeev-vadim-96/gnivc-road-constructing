package ru.gnivc.dwhservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.dwhservice.dto.StatisticsByCompanyDto;

public interface DwhService {
    ResponseEntity<StatisticsByCompanyDto> getStatisticsByCompany(String companyName);
}
