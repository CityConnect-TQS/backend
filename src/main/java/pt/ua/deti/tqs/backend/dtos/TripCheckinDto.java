package pt.ua.deti.tqs.backend.dtos;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import pt.ua.deti.tqs.backend.constants.TripStatus;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.entities.Reservation;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TripCheckinDto {
    
    private long id;

    private City departure;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    private City arrival;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    private double price;

    private Bus bus;

    private int freeSeats;

    private TripStatus status;

    private int delay;

    private int checkedInSeats;

    public TripCheckinDto(Trip trip) {
        this.id = trip.getId();
        this.departure = trip.getDeparture();
        this.departureTime = trip.getDepartureTime();
        this.arrival = trip.getArrival();
        this.arrivalTime = trip.getArrivalTime();
        this.price = trip.getPrice();
        this.bus = trip.getBus();
        this.freeSeats = trip.getFreeSeats();
        this.status = trip.getStatus();
        this.delay = trip.getDelay();
        Collection<Reservation> reservations = trip.getReservations();

        this.checkedInSeats = (reservations != null ? reservations.stream()
                                                                   .reduce(0,
                                                                    (acc, r) -> acc + (r.isCheckedIn() ? r.getSeats().size(): 0),
                                                                    Integer::sum) : 0);
    }
}
