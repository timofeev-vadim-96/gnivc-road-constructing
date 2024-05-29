package ru.gnivc.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dto.input.VehicleDto;
import ru.gnivc.portalservice.model.VehicleEntity;
import ru.gnivc.portalservice.service.VehicleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("portal/v1/company/vehicle")
public class VehicleController {
    private final VehicleService vehicleService;

    /**
     * Registration of the company's vehicle
     */
    @PostMapping
    public ResponseEntity<VehicleEntity> registerVehicle(
            @Valid @RequestBody VehicleDto vehicleDto,
            @RequestParam String companyId){

        VehicleEntity vehicle = vehicleService.save(vehicleDto, companyId);
        if (vehicle != null) {
            return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The company with id = " + companyId + " was not found");
        }
    }
}
