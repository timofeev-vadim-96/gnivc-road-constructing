package ru.gnivc.logistservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gnivc.logistservice.util.TripEventEnum;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripEventDto {
    @NotNull
    private Long tripId;

    @NotNull
    private TripEventEnum tripEventEnum;

    @NotNull
    private LocalDateTime time;
}
