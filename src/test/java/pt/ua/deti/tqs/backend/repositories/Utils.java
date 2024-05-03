package pt.ua.deti.tqs.backend.repositories;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.entities.*;

import java.time.LocalDateTime;

public class Utils {
    public static Bus generateBus(TestEntityManager entityManager) {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus.setCompany("Flexibus");
        entityManager.persistAndFlush(bus);
        return bus;
    }

    public static City generateCity(TestEntityManager entityManager) {
        City city = new City();
        city.setName("Aveiro");
        entityManager.persistAndFlush(city);
        return city;
    }

    public static User generateUser(TestEntityManager entityManager) {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        entityManager.persistAndFlush(user);
        return user;
    }

    public static Trip generateTrip(TestEntityManager entityManager) {
        Trip trip = new Trip();
        trip.setBus(generateBus(entityManager));
        trip.setDeparture(generateCity(entityManager));
        trip.setArrival(generateCity(entityManager));
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(50);
        entityManager.persistAndFlush(trip);
        return trip;
    }

    public static Reservation generateReservation(TestEntityManager entityManager) {
        Reservation reservation = new Reservation();
        reservation.setTrip(generateTrip(entityManager));
        reservation.setUser(generateUser(entityManager));
        reservation.setPrice(50);
        reservation.setSeats(2);
        entityManager.persistAndFlush(reservation);
        return reservation;
    }
}
