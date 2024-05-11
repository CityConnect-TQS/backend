package pt.ua.deti.tqs.backend.dtos;

import java.time.LocalDateTime;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;

import java.util.ArrayList;
import java.util.Collection;

import pt.ua.deti.tqs.backend.constants.TripStatus;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.entities.City;

@Slf4j
@Getter
@Setter
public class TripSeatsMapDto {

    @AllArgsConstructor
    @Getter
    @Setter
    public class Seat{
        int id;   // row id
        boolean isAlreadyReserved;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class SeatsMap{
        char id;  // "column" id, from start to end of the bus
        List<Seat> seats;
    }

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

    private List<SeatsMap> seatsMap = new ArrayList<SeatsMap>();

    public TripSeatsMapDto(Trip trip){
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
        
        int capacity = trip.getBus().getCapacity();
        int numRows = capacity / 4;
        char[] columns = {'A', 'B', 'D', 'E'};
        Collection<Reservation> tripReservations = trip.getReservations() != null ? trip.getReservations() : new ArrayList<Reservation>();
        ArrayList<String> reservedSeats = new ArrayList<String>();

        for (Reservation savedReservation : tripReservations) {
            reservedSeats.addAll(savedReservation.getSeats());
        }

        log.info("numRows: " + numRows);

        for(int col = 1; col <= 4; col++){
            List<Seat> seats = new ArrayList<Seat>();
            for(int row = 1; row <= numRows; row++){
                if(reservedSeats.contains((Integer.toString(row) + columns[col-1]))){
                    seats.add(new Seat(row, true));
                }
                else{
                    seats.add(new Seat(row, false));
                }     
            }
            this.seatsMap.add(new SeatsMap(columns[col-1], seats));           
        }

        // add last seat on row of 5 if capacity is not a multiple of 4
        if(capacity % 4 != 0){
            log.info("Adding last row of 5 seats");
            List<Seat> seats = new ArrayList<Seat>();
            if(reservedSeats.contains((Integer.toString(numRows) + 'C'))){
                seats.add(new Seat(numRows, true));
            }
            else{
                seats.add(new Seat(numRows, false));
            }     
            this.seatsMap.add(new SeatsMap('C', seats)); 
        }

        log.info("" + seatsMap.size());

    }
}
