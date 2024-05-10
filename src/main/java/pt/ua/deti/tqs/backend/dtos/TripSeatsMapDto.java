package pt.ua.deti.tqs.backend.dtos;

import java.time.LocalDateTime;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Getter
@Setter
@AllArgsConstructor
public class TripSeatsMapDto {

    class Seat{
        String id;   // row id
        boolean isAlreadyReserved;
    }

    class SeatsMap{
        int id;  // "column" id, from start to end of the bus
        List<Seat> seats;
    }

    private long id;

    private long departureId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    private long arrivalId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    private double price;

    private long busId;

    private int freeSeats;

    private SeatsMap seatsMap;
}
