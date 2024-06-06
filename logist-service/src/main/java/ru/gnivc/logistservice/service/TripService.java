package ru.gnivc.logistservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.logistservice.dao.TaskDao;
import ru.gnivc.logistservice.dao.TripDao;
import ru.gnivc.logistservice.dao.TripEventDao;
import ru.gnivc.logistservice.dao.TripLocationDao;
import ru.gnivc.logistservice.dto.output.TripDto;
import ru.gnivc.logistservice.mapper.TripMapper;
import ru.gnivc.logistservice.model.TaskEntity;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripDao tripDao;
    private final TaskDao taskDao;
    private final TripEventDao tripEventDao;
    private final TripLocationDao tripLocationDao;

    @Transactional
    public ResponseEntity<TripEntity> create(long taskId, String companyName) {
        Optional<TaskEntity> task = taskDao.findById(taskId);
        if (task.isEmpty()) {
            String answer = String.format("Task with id = %s not found", taskId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!task.get().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format("The task with id = %s was created by a logistics specialist from another company", taskId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            TripEntity trip = TripEntity.builder()
                    .creationTime(LocalDateTime.now())
                    .task(task.get())
                    .build();
            TripEntity savedTrip = tripDao.save(trip);

            TripEventEntity tripEvent = TripEventEntity.builder()
                    .event(TripEventEnum.TRIP_CREATED.name())
                    .time(LocalDateTime.now())
                    .trip(savedTrip)
                    .build();
            tripEventDao.save(tripEvent);

            return new ResponseEntity<>(savedTrip, HttpStatus.CREATED);
        }
    }

    public ResponseEntity<TripDto> get(long tripId, String companyName) {
        Optional<TripEntity> trip = tripDao.findById(tripId);
        if (trip.isEmpty()) {
            String answer = String.format("Trip with id = %s not found", tripId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!trip.get().getTask().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format("The trip with id = %s was created by a logistics specialist from another company", trip);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            TripEventEntity lastEvent = getLatestEvent(trip.get());
            TripDto tripDto = TripMapper.convertTripToDto(trip.get(), lastEvent);
            return new ResponseEntity<>(tripDto, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<TripEntity>> getAllByTask(long taskId, String companyName) {
        Optional<TaskEntity> task = taskDao.findById(taskId);
        if (task.isEmpty()) {
            String answer = String.format("Task with id = %s not found", taskId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!task.get().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format("The task with id = %s was created by a logistics specialist from another company", taskId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            List<TripEntity> tripsByTask = tripDao.getAllByTask(task.get());
            return new ResponseEntity<>(tripsByTask, HttpStatus.OK);
        }
    }

    public ResponseEntity<Void> removeById(long tripId, String companyName) {
        Optional<TripEntity> trip = tripDao.findById(tripId);
        if (trip.isEmpty()) {
            String answer = String.format("Trip with id = %s not found", tripId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!trip.get().getTask().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format("The trip with id = %s was created by a logistics specialist from another company", trip);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            tripDao.delete(trip.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    private TripEventEntity getLatestEvent(TripEntity trip) {
        List<TripEventEntity> events = tripEventDao.findAllByTrip(trip);
        return getLatestEvent(events);
    }
    private TripEventEntity getLatestEvent(List<TripEventEntity> events) {
        TripEventEntity lastEvent = events.getFirst();
        for (TripEventEntity event : events) {
            if (lastEvent.getTime().isBefore(event.getTime())) {
                lastEvent = event;
            }
        }
        return lastEvent;
    }

    public ResponseEntity<List<TripEventEntity>> getTripWithEvents(long tripId, String companyName) {
        Optional<TripEntity> trip = tripDao.findById(tripId);
        if (trip.isEmpty()) {
            String answer = String.format("Trip with id = %s not found", tripId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!trip.get().getTask().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format("The trip with id = %s was created by a logistics specialist from another company", trip);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            List<TripEventEntity> tripEvents = tripEventDao.findAllByTrip(trip.get());
            return new ResponseEntity<>(tripEvents, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<TripLocationEntity>> getTripWithLocaitonPoints(long tripId, String companyName) {
        Optional<TripEntity> trip = tripDao.findById(tripId);
        if (trip.isEmpty()) {
            String answer = String.format("Trip with id = %s not found", tripId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        } else if (!trip.get().getTask().getCompany().getCompanyName().equals(companyName)) {
            String answer = String.format("The trip with id = %s was created by a logistics specialist from another company", trip);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, answer);
        } else {
            List<TripLocationEntity> locationPoints = tripLocationDao.findAllByTrip(trip.get());
            return new ResponseEntity<>(locationPoints, HttpStatus.OK);
        }
    }
}
