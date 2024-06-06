package ru.gnivc.logistservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "trip_events")
public class TripEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String event;
    @NotNull
    private LocalDateTime time;
    @NotNull
    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "trip")
    private TripEntity trip;

    public TripEventEntity(String event, LocalDateTime time, TripEntity trip) {
        this.event = event;
        this.time = time;
        this.trip = trip;
    }
}
