package pt.ua.deti.tqs.backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.constants.TripStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TripRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TripRepository tripRepository;

    @Test
    void whenFindTripById_thenReturnTrip() {
        City city = Utils.generateCity(entityManager);
        Bus bus = Utils.generateBus(entityManager);

        Trip trip = new Trip();
        trip.setDeparture(city);
        trip.setArrival(city);
        trip.setBus(bus);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(50);
        entityManager.persistAndFlush(trip);

        Trip found = tripRepository.findById(trip.getId()).orElse(null);
        assertThat(found).isEqualTo(trip);
    }

    @Test
    void whenInvalidTripId_thenReturnNull() {
        Trip fromDb = tripRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllTrips_thenReturnAllTrips() {
        City city = Utils.generateCity(entityManager);
        Bus bus = Utils.generateBus(entityManager);

        Trip trip1 = new Trip();
        trip1.setDeparture(city);
        trip1.setArrival(city);
        trip1.setBus(bus);
        trip1.setDepartureTime(LocalDateTime.now());
        trip1.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip1.setPrice(50);
        Trip trip2 = new Trip();
        trip2.setDeparture(city);
        trip2.setArrival(city);
        trip2.setBus(bus);
        trip2.setDepartureTime(LocalDateTime.now());
        trip2.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip2.setPrice(100);
        Trip trip3 = new Trip();
        trip3.setDeparture(city);
        trip3.setArrival(city);
        trip3.setBus(bus);
        trip3.setDepartureTime(LocalDateTime.now());
        trip3.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip3.setPrice(50);

        entityManager.persist(trip1);
        entityManager.persist(trip2);
        entityManager.persist(trip3);

        Iterable<Trip> allTrips = tripRepository.findAll();
        assertThat(allTrips).hasSize(3).contains(trip1, trip2, trip3);
    }

    @Test
    void whenCreatTrip_thenTripShouldBeCreated() {
        City city = Utils.generateCity(entityManager);
        Bus bus = Utils.generateBus(entityManager);

        Trip trip = new Trip();
        trip.setDeparture(city);
        trip.setArrival(city);
        trip.setBus(bus);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(50);
        entityManager.persistAndFlush(trip);

        assertThat(trip.getId()).isNotNull();
        Trip found = tripRepository.findById(trip.getId()).orElse(null);
        assertThat(found).isEqualTo(trip);
        assertThat(found.getStatus()).isEqualTo(TripStatus.ONTIME);
    }

    @Test
    void whenDeleteTripById_thenTripShouldNotExist() {
        City city = Utils.generateCity(entityManager);
        Bus bus = Utils.generateBus(entityManager);

        Trip trip = new Trip();
        trip.setDeparture(city);
        trip.setArrival(city);
        trip.setBus(bus);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(50);
        entityManager.persistAndFlush(trip);

        tripRepository.deleteById(trip.getId());
        assertThat(tripRepository.findById(trip.getId())).isEmpty();
    }

    @Test
    void whenUpdateTrip_thenTripShouldBeUpdated() {
        City city = Utils.generateCity(entityManager);
        Bus bus = Utils.generateBus(entityManager);

        Trip trip = new Trip();
        trip.setDeparture(city);
        trip.setArrival(city);
        trip.setBus(bus);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(50);
        entityManager.persistAndFlush(trip);

        trip.setPrice(100);
        tripRepository.save(trip);

        Trip updatedTrip = tripRepository.findById(trip.getId()).orElse(null);
        assertThat(updatedTrip).isNotNull();
        assertThat(updatedTrip.getPrice()).isEqualTo(100);
    }
}
