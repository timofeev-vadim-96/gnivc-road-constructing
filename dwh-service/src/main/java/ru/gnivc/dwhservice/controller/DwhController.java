package ru.gnivc.dwhservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gnivc.dwhservice.dto.StatisticsByCompanyDto;
import ru.gnivc.dwhservice.service.DwhService;

@RestController
@RequestMapping("/dwh/v1/statistics")
@RequiredArgsConstructor
public class DwhController {
    private final DwhService service;

    @GetMapping()
    public ResponseEntity<StatisticsByCompanyDto> getCancelledTripsQuantity(@RequestParam String companyName){
        return service.getStatisticsByCompany(companyName);
    }
}
