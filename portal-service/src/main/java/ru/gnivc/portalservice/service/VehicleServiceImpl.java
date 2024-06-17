package ru.gnivc.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dao.CompanyDao;
import ru.gnivc.portalservice.dao.VehicleDao;
import ru.gnivc.portalservice.dto.input.VehicleDto;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.model.VehicleEntity;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleDao vehicleDao;
    private final CompanyDao companyDao;

    public VehicleEntity save(VehicleDto vehicleDto, String companyName) {
        Optional<CompanyEntity> company = companyDao.findByName(companyName);
        if (company.isEmpty()) return null;
        else {
            VehicleEntity vehicle = VehicleEntity.builder()
                    .vin(vehicleDto.getVin())
                    .releaseYear(vehicleDto.getReleaseYear())
                    .company(company.get())
                    .stateNumber(vehicleDto.getStateNumber())
                    .build();
            return vehicleDao.save(vehicle);
        }
    }

    public ResponseEntity<VehicleEntity> getVehicleById(String companyName, long id) {
        Optional<VehicleEntity> vehicle = vehicleDao.findById(id);
        if (vehicle.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Vehicle with id = %s not found", id));
        } else if (!vehicle.get().getCompany().getName().equals(companyName)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    String.format("The vehicle with id = %s belongs to another company", id));
        } else {
            return new ResponseEntity<>(vehicle.get(), HttpStatus.OK);
        }
    }

    public ResponseEntity<List<VehicleEntity>> getCompanyVehicles(String companyName) {
        Optional<CompanyEntity> company = companyDao.findByName(companyName);
        if (company.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Company with name = %s not found", companyName));
        } else {
            List<VehicleEntity> vehicles = vehicleDao.findAllByCompanyId(company.get().getId());
            return new ResponseEntity<>(vehicles, HttpStatus.OK);
        }
    }

    public ResponseEntity<Void> removeVehicleById(String companyName, long vehicleId) {
        Optional<VehicleEntity> vehicle = vehicleDao.findById(vehicleId);
        if (vehicle.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Vehicle with id = %s not found", vehicleId));
        } else if (!vehicle.get().getCompany().getName().equals(companyName)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    String.format("The vehicle with id = %s belongs to another company", vehicleId));
        } else {
            vehicleDao.delete(vehicle.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
