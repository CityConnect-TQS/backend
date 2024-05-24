package pt.ua.deti.tqs.backend.specifications.trip;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

import pt.ua.deti.tqs.backend.dtos.TripCheckinDto;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;

public class TripCheckinDtoTest {
    
    @Test
    public void testCheckInDtoConstructor() throws NoSuchFieldError, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Aveiro");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Porto");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(49);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(10.0);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setBus(bus);
        
        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setSeats(Arrays.asList("1A"));
        reservation1.setTrip(trip);
        reservation1.setPrice(10.0);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setSeats(Arrays.asList("1B", "1C"));
        reservation2.setTrip(trip);
        reservation2.setPrice(20.0);
        reservation2.setCheckedIn(true);

        Reservation reservation3 = new Reservation();
        reservation3.setId(3L);
        reservation3.setSeats(Arrays.asList("2A", "2B", "2C"));
        reservation3.setTrip(trip);
        reservation3.setPrice(30.0);
        reservation3.setCheckedIn(true);

        trip.calculateFreeSeats();
        Field reservationsField = Trip.class.getDeclaredField("reservations");
        reservationsField.setAccessible(true);
        reservationsField.set(trip, Arrays.asList(reservation1, reservation2, reservation3));

        TripCheckinDto tripCheckinDto = new TripCheckinDto(trip);
        assertNotNull(tripCheckinDto);
        assertThat(tripCheckinDto.getCheckedInSeats()).isEqualTo(5);
    }


    @Test
    public void testCheckInDtoConstructor2() {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Aveiro");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Porto");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(49);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(10.0);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setBus(bus);
        
        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setSeats(Arrays.asList("1A"));
        reservation1.setTrip(trip);
        reservation1.setPrice(10.0);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setSeats(Arrays.asList("1B", "1C"));
        reservation2.setTrip(trip);
        reservation2.setPrice(20.0);

        Reservation reservation3 = new Reservation();
        reservation3.setId(3L);
        reservation3.setSeats(Arrays.asList("2A", "2B", "2C"));
        reservation3.setTrip(trip);
        reservation3.setPrice(30.0);
        reservation3.setCheckedIn(true);

        trip.calculateFreeSeats();

        TripCheckinDto tripCheckinDto = new TripCheckinDto(trip);
        assertNotNull(tripCheckinDto);
        assertThat(tripCheckinDto.getCheckedInSeats()).isEqualTo(0);
    }

    @Test
    public void testCheckInDtoConstructor3() throws NoSuchFieldError, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Aveiro");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Porto");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(49);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(10.0);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setBus(bus);
        
        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setSeats(Arrays.asList("1A"));
        reservation1.setTrip(trip);
        reservation1.setPrice(10.0);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setSeats(Arrays.asList("1B", "1C"));
        reservation2.setTrip(trip);
        reservation2.setPrice(20.0);

        Reservation reservation3 = new Reservation();
        reservation3.setId(3L);
        reservation3.setSeats(Arrays.asList("2A", "2B", "2C"));
        reservation3.setTrip(trip);
        reservation3.setPrice(30.0);

        trip.calculateFreeSeats();
        Field reservationsField = Trip.class.getDeclaredField("reservations");
        reservationsField.setAccessible(true);
        reservationsField.set(trip, Arrays.asList(reservation1, reservation2, reservation3));

        TripCheckinDto tripCheckinDto = new TripCheckinDto(trip);
        assertNotNull(tripCheckinDto);
        assertThat(tripCheckinDto.getCheckedInSeats()).isEqualTo(0);
    }

}
