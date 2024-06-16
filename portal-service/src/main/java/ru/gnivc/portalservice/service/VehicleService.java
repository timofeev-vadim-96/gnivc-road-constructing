package ru.gnivc.portalservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.portalservice.dto.input.VehicleDto;
import ru.gnivc.portalservice.model.VehicleEntity;

import java.util.List;

public interface VehicleService {
    VehicleEntity save(VehicleDto vehicleDto, String companyName);
    ResponseEntity<VehicleEntity> getVehicleById(String companyName, long id);
    ResponseEntity<List<VehicleEntity>> getCompanyVehicles(String companyName);
    ResponseEntity<Void> removeVehicleById(String companyName, long vehicleId);
}
