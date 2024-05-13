package pt.ua.deti.tqs.backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.entities.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void whenFindReservationById_thenReturnReservation() {
        Trip trip = Utils.generateTrip(entityManager);
        User user = Utils.generateUser(entityManager);

        Reservation reservation = new Reservation();
        reservation.setPrice(50);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(Arrays.asList("1A", "1B"));
        entityManager.persistAndFlush(reservation);

        Reservation found = reservationRepository.findById(reservation.getId()).orElse(null);
        assertThat(found).isEqualTo(reservation);
    }

    @Test
    void whenFindReservationByUserId_thenReturnReservation() {
        Trip trip = Utils.generateTrip(entityManager);
        User user = Utils.generateUser(entityManager);

        Reservation reservation = new Reservation();
        reservation.setPrice(50);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(Arrays.asList("1A", "1B"));
        entityManager.persistAndFlush(reservation);

        List<Reservation> found = reservationRepository.findByUserId(user.getId());
        assertThat(found).hasSize(1).contains(reservation);
    }

    @Test
    void whenFindReservationByTripId_thenReturnReservation() {
        Trip trip = Utils.generateTrip(entityManager);
        User user = Utils.generateUser(entityManager);

        Reservation reservation = new Reservation();
        reservation.setPrice(50);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(Arrays.asList("1A", "1B"));
        entityManager.persistAndFlush(reservation);

        List<Reservation> found = reservationRepository.findByTripId(trip.getId());
        assertThat(found).hasSize(1).contains(reservation);
    }

    @Test
    void whenInvalidReservationId_thenReturnNull() {
        Reservation fromDb = reservationRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllReservations_thenReturnAllReservations() {
        Trip trip = Utils.generateTrip(entityManager);
        User user = Utils.generateUser(entityManager);

        Reservation reservation1 = new Reservation();
        reservation1.setPrice(50);
        reservation1.setTrip(trip);
        reservation1.setUser(user);
        reservation1.setSeats(Arrays.asList("1A", "1B"));
        Reservation reservation2 = new Reservation();
        reservation2.setPrice(100);
        reservation2.setTrip(trip);
        reservation2.setUser(user);
        reservation2.setSeats(Arrays.asList("1A", "1B", "1C", "1D"));
        Reservation reservation3 = new Reservation();
        reservation3.setPrice(50);
        reservation3.setTrip(trip);
        reservation3.setUser(user);
        reservation3.setSeats(Arrays.asList("1A", "1B"));

        entityManager.persist(reservation1);
        entityManager.persist(reservation2);
        entityManager.persist(reservation3);

        Iterable<Reservation> allReservations = reservationRepository.findAll();
        assertThat(allReservations).hasSize(3).contains(reservation1, reservation2, reservation3);
    }

    @Test
    void whenDeleteReservationById_thenReservationShouldNotExist() {
        Trip trip = Utils.generateTrip(entityManager);
        User user = Utils.generateUser(entityManager);

        Reservation reservation = new Reservation();
        reservation.setPrice(50);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(Arrays.asList("1A", "1B"));
        entityManager.persistAndFlush(reservation);

        reservationRepository.deleteById(reservation.getId());
        assertThat(reservationRepository.findById(reservation.getId())).isEmpty();
    }

}
