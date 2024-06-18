package ru.gnivc.logistservice.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.gnivc.logistservice.dto.output.TripDto;
import ru.gnivc.logistservice.model.TripEntity;
import ru.gnivc.logistservice.model.TripEventEntity;
import ru.gnivc.logistservice.model.TripLocationEntity;
import ru.gnivc.logistservice.service.TripService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logist/v1/trip")
public class TripController {
    private final TripService tripService;

    /**
     * Get all trips by task
     */
    @GetMapping("/list")
    public ResponseEntity<List<TripEntity>> getAllByTask(@RequestParam long taskId,
                                                         @RequestParam String companyName) {
        return tripService.getAllByTask(taskId, companyName);
    }

    /**
     * Get trip by id
     */
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDto> get(@PathVariable long tripId,
                                       @RequestParam String companyName) {
        return tripService.get(tripId, companyName);
    }

    @GetMapping("/{tripId}/events")
    public ResponseEntity<List<TripEventEntity>> getTripEvents(@PathVariable long tripId,
                                                               @RequestParam String companyName) {
        return tripService.getTripWithEvents(tripId, companyName);
    }

    @GetMapping("/{tripId}/locations")
    public ResponseEntity<List<TripLocationEntity>> getTripLocationPoints(@PathVariable long tripId,
                                                                          @RequestParam String companyName) {
        return tripService.getTripWithLocaitonPoints(tripId, companyName);
    }

    @PostMapping
    public ResponseEntity<TripEntity> create(@RequestParam long taskId,
                                             @RequestParam String companyName) {
        return tripService.create(taskId, companyName);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> remove(@PathVariable long tripId,
                                       @RequestParam String companyName) {
        return tripService.removeById(tripId, companyName);
    }
}
