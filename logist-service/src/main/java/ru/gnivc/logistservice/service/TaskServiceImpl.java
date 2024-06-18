package ru.gnivc.logistservice.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.logistservice.dao.CompanyDao;
import ru.gnivc.logistservice.dao.DriverDao;
import ru.gnivc.logistservice.dao.TaskDao;
import ru.gnivc.logistservice.dao.VehicleDao;
import ru.gnivc.logistservice.dto.input.CompanyDto;
import ru.gnivc.logistservice.dto.input.DriverDto;
import ru.gnivc.logistservice.dto.input.TaskDto;
import ru.gnivc.logistservice.dto.input.VehicleDto;
import ru.gnivc.logistservice.model.CompanyEntity;
import ru.gnivc.logistservice.model.DriverEntity;
import ru.gnivc.logistservice.model.TaskEntity;
import ru.gnivc.logistservice.model.VehicleEntity;
import ru.gnivc.logistservice.provider.PortalProvider;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final PortalProvider portalProvider;

    private final CompanyDao companyDao;

    private final DriverDao driverDao;

    private final VehicleDao vehicleDao;

    private final TaskDao taskDao;

    public ResponseEntity<Void> removeById(long taskId, String companyName) {
        Optional<TaskEntity> taskOptional = taskDao.findById(taskId);
        if (taskOptional.isEmpty()) {
            String answer = String.format("Task with id = %s not found", taskId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!taskOptional.get().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format(
                    "The task with id = %s was created by a logistics specialist from another company",
                    taskId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            TaskEntity task = taskOptional.get();

            taskDao.delete(task);

            removeOrphanEntities(task.getCompany(), task.getDriver(), task.getVehicle());

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    public ResponseEntity<List<TaskEntity>> findAllByCompanyName(String companyName) {
        try {
            CompanyEntity company = getCompany(companyName);
            return new ResponseEntity<>(taskDao.findAllByCompany(company), HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<TaskEntity> findById(long taskId, String companyName) {
        Optional<TaskEntity> task = taskDao.findById(taskId);
        if (task.isEmpty()) {
            String answer = String.format("Task with id = %s not found", taskId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!task.get().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format(
                    "The task with id = %s was created by a logistics specialist from another company",
                    taskId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            return new ResponseEntity<>(task.get(), HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<TaskEntity> create(TaskDto taskDto, String companyName) {
        try {
            CompanyEntity company = getCompany(companyName);
            DriverEntity driver = getDriver(taskDto.getDriverId(), companyName);
            VehicleEntity vehicle = getVehicle(taskDto.getVehicleId(), companyName);

            TaskEntity task = TaskEntity.builder()
                    .company(company)
                    .driver(driver)
                    .vehicle(vehicle)
                    .startPoint(taskDto.getStartPoint())
                    .finishPoint(taskDto.getFinishPoint())
                    .cargoDescription(taskDto.getCargoDescription())
                    .build();
            TaskEntity savedTask = taskDao.save(task);
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<List<TaskEntity>> getTaskByDriver(long driverId, String companyName) {
        try {
            DriverEntity driver = getDriver(driverId, companyName);
            List<TaskEntity> driverTasks = taskDao.findAllByDriver(driver);
            return new ResponseEntity<>(driverTasks, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CompanyEntity getCompany(String companyName) throws NotFoundException {
        Optional<CompanyEntity> company = companyDao.findByCompanyName(companyName);
        if (company.isEmpty()) {
            CompanyDto companyFromPortal = portalProvider.getCompanyByName(companyName);
            if (companyFromPortal == null) {
                throw new NotFoundException("Company with companyName = " + companyName + " not found in portal-ms");
            } else {
                CompanyEntity newCompany = new CompanyEntity(companyFromPortal.getId(), companyName);
                CompanyEntity saved = companyDao.save(newCompany);
                log.info("saved company: {}", saved);
                return saved;
            }
        } else {
            return company.get();
        }
    }

    private DriverEntity getDriver(long driverId, String companyName) throws NotFoundException {
        Optional<DriverEntity> driver = driverDao.findById(driverId);
        if (driver.isEmpty()) {
            DriverDto driverFromPortal = portalProvider.getDriverById(driverId, companyName);
            if (driverFromPortal == null) {
                throw new NotFoundException("Driver with id = " + driverId + " not found in portal-ms");
            } else {
                DriverEntity newDriver = DriverEntity.builder()
                        .id(driverFromPortal.getId())
                        .firstName(driverFromPortal.getFirstName())
                        .lastName(driverFromPortal.getLastName())
                        .build();
                DriverEntity saved = driverDao.save(newDriver);
                log.info("saved driver: {}", saved);
                return saved;
            }
        } else {
            return driver.get();
        }
    }

    private VehicleEntity getVehicle(long vehicleId, String companyName) throws NotFoundException {
        Optional<VehicleEntity> vehicle = vehicleDao.findById(vehicleId);
        if (vehicle.isEmpty()) {
            VehicleDto vehicleFromPortal = portalProvider.getVehicleById(vehicleId, companyName);
            if (vehicleFromPortal == null) {
                throw new NotFoundException("Vehicle with id = " + vehicleId + " not found in portal-ms");
            } else {
                VehicleEntity newVehicle = new VehicleEntity(
                        vehicleFromPortal.getId(),
                        vehicleFromPortal.getStateNumber());

                VehicleEntity saved = vehicleDao.save(newVehicle);
                log.info("saved vehicle: {}", saved);
                return saved;
            }
        } else {
            return vehicle.get();
        }
    }

    private void removeOrphanEntities(CompanyEntity company, DriverEntity driver, VehicleEntity vehicle) {
        List<TaskEntity> tasks = taskDao.findAllByCompany(company);

        int companyCounter = 0;
        int driverCounter = 0;
        int vehicleCounter = 0;
        for (TaskEntity task : tasks) {
            if (companyCounter > 0 && driverCounter > 0 && vehicleCounter > 0) {
                break;
            }

            if (task.getCompany().equals(company)) {
                companyCounter++;
            }
            if (task.getDriver().equals(driver)) {
                driverCounter++;
            }
            if (task.getVehicle().equals(vehicle)) {
                vehicleCounter++;
            }
        }
        removeCompanyOrphan(company, companyCounter);
        removeDriverOrphan(driver, driverCounter);
        removeVehicleOrphan(vehicle, vehicleCounter);
    }

    private void removeCompanyOrphan(CompanyEntity company, int companyCounter) {
        if (companyCounter == 0) {
            companyDao.delete(company);
        }
    }

    private void removeDriverOrphan(DriverEntity driver, int driverCounter) {
        if (driverCounter == 0) {
            driverDao.delete(driver);
        }
    }

    private void removeVehicleOrphan(VehicleEntity vehicle, int vehicleCounter) {
        if (vehicleCounter == 0) {
            vehicleDao.delete(vehicle);
        }
    }
}
