package pt.ua.deti.tqs.backend.specifications.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("java:S1068")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripSearchParameters implements Serializable {
    @Serial
    private static final long serialVersionUID = 2405172041950251807L;

    private Long departure;
    private Long arrival;
    private LocalDateTime departureTime;
    private Long freeSeats;
}