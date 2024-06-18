package ru.gnivc.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dto.input.VehicleDto;
import ru.gnivc.portalservice.model.VehicleEntity;
import ru.gnivc.portalservice.service.VehicleService;

import java.util.List;

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
            @RequestParam String companyName) {

        VehicleEntity vehicle = vehicleService.save(vehicleDto, companyName);
        if (vehicle != null) {
            return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The company with name = " + companyName + " was not found");
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleEntity> getVehicleById(@PathVariable long vehicleId,
                                                        @RequestParam String companyName) {
        return vehicleService.getVehicleById(companyName, vehicleId);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> removeVehicleById(@PathVariable long vehicleId,
                                                  @RequestParam String companyName) {
        return vehicleService.removeVehicleById(companyName, vehicleId);
    }

    /**
     * Getting a list of company vehicles
     */
    @GetMapping("/list")
    public ResponseEntity<List<VehicleEntity>> getCompanyVehicles(@RequestParam String companyName) {
        return vehicleService.getCompanyVehicles(companyName);
    }
}
