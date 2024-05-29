package ru.gnivc.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gnivc.portalservice.dao.CompanyDao;
import ru.gnivc.portalservice.dao.VehicleDao;
import ru.gnivc.portalservice.dto.input.VehicleDto;
import ru.gnivc.portalservice.model.CompanyEntity;
import ru.gnivc.portalservice.model.VehicleEntity;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleDao vehicleDao;
    private final CompanyDao companyDao;

    public VehicleEntity save(VehicleDto vehicleDto, String companyId) {
        Optional<CompanyEntity> company = companyDao.findByName(companyId);
        if (company.isEmpty()) return null;
        else {
            VehicleEntity vehicle = VehicleEntity.builder()
                    .vin(vehicleDto.getVin())
                    .releaseYear(vehicleDto.getReleaseYear())
                    .company(company.get())
                    .build();
            return vehicleDao.save(vehicle);
        }
    }
}
