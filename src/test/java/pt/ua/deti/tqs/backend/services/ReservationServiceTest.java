package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.backend.entities.*;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.repositories.ReservationRepository;
import pt.ua.deti.tqs.backend.repositories.TripRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock(lenient = true)
    private ReservationRepository reservationRepository;

    @Mock(lenient = true)
    private TripRepository tripRepository;

    @Mock(lenient = true)
    private CurrencyService currencyService;

    @Mock(lenient = true)
    private TripService tripService;

    @Mock(lenient = true)
    private UserService userService;

    @InjectMocks
    private ReservationService reservationService;

    Trip trip;
    User user;

    @BeforeEach
    public void setUp() {
        City city = new City();
        city.setId(2L);
        city.setName("Porto");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);

        trip = new Trip();
        trip.setId(1L);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setPrice(10.0);
        trip.setDeparture(city);
        trip.setArrival(city);
        trip.setBus(bus);
        trip.setFreeSeats(bus.getCapacity());

        user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setEmail("user@ua.pt");
        user.setName("User");

        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setSeats(Arrays.asList("1A"));
        reservation1.setTrip(trip);
        reservation1.setUser(user);
        reservation1.setPrice(10.0);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setSeats(Arrays.asList("1B", "1C"));
        reservation2.setTrip(trip);
        reservation2.setUser(null);
        reservation2.setPrice(20.0);

        Reservation reservation3 = new Reservation();
        reservation3.setId(3L);
        reservation3.setSeats(Arrays.asList("2A", "2B", "2C"));
        reservation3.setTrip(trip);
        reservation3.setUser(user);
        reservation3.setPrice(30.0);

        Mockito.when(reservationRepository.findById(reservation1.getId())).thenReturn(Optional.of(reservation1));
        Mockito.when(reservationRepository.findById(12345L)).thenReturn(Optional.empty());
        Mockito.when(reservationRepository.findAll()).thenReturn(List.of(reservation1, reservation2, reservation3));
        Mockito.when(reservationRepository.findByUserId(user.getId()))
               .thenReturn(List.of(reservation1, reservation3));
        Mockito.when(reservationRepository.findByTripId(trip.getId()))
               .thenReturn(List.of(reservation1, reservation2));
        Mockito.when(currencyService.convertEurToCurrency(10.0, Currency.USD)).thenReturn(11.0);
        Mockito.when(currencyService.convertEurToCurrency(20.0, Currency.USD)).thenReturn(22.0);
        Mockito.when(currencyService.convertEurToCurrency(30.0, Currency.USD)).thenReturn(33.0);
        Mockito.when(tripService.getTrip(trip.getId(), null)).thenReturn(trip);
        Mockito.when(userService.getUser(user.getId())).thenReturn(user);
        Mockito.when(reservationRepository.save(Mockito.any())).thenReturn(reservation1);
    }

    @Test
    void whenSearchValidId_thenReservationShouldBeFound() {
        Reservation found = reservationService.getReservation(1L, null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    void whenSearchValidIdAndCurrencyUsd_thenReservationShouldBeFound() {
        Reservation found = reservationService.getReservation(1L, Currency.USD);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getPrice()).isEqualTo(11.0);
    }

    @Test
    void whenSearchInvalidId_thenReservationShouldNotBeFound() {
        Reservation found = reservationService.getReservation(12345L, null);

        assertThat(found).isNull();
    }

    @Test
    void whenFindAll_thenAllReservationsShouldBeFound() {
        List<Reservation> reservations = reservationService.getAllReservations(null);

        assertThat(reservations).isNotNull().hasSize(3);
    }

    @Test
    void whenFindAllWithCurrencyUsd_thenAllReservationsShouldBeFound() {
        List<Reservation> reservations = reservationService.getAllReservations(Currency.USD);

        assertThat(reservations).isNotNull().hasSize(3);
        assertThat(reservations.get(0).getPrice()).isEqualTo(11.0);
        assertThat(reservations.get(1).getPrice()).isEqualTo(22.0);
        assertThat(reservations.get(2).getPrice()).isEqualTo(33.0);
    }

    @Test
    void whenFindByUserId_thenReservationsShouldBeFound() {
        List<Reservation> reservations = reservationService.getReservationsByUserId(1L, null);

        assertThat(reservations).isNotNull().hasSize(2);
    }

    @Test
    void whenFindByTripId_thenNoReservationsShouldBeFound() {
        List<Reservation> reservations = reservationService.getReservationsByTripId(1L, null);

        assertThat(reservations).isNotNull().hasSize(2);
    }

    @Test
    void whenCreateValidReservation_thenReservationShouldBeCreated() {
        Reservation reservation4 = new Reservation();
        reservation4.setId(4L);
        reservation4.setSeats(Arrays.asList("3A"));
        reservation4.setTrip(trip);
        reservation4.setPrice(30.0);
        reservation4.setUser(user);

        Mockito.when(reservationRepository.save(reservation4)).thenReturn(reservation4);

        Reservation saved = reservationService.createReservation(reservation4, null);

        assertThat(saved).isNotNull();
        assertThat(saved).isEqualTo(reservation4);
    }

    @Test
    void whenCreateInvalidReservation_thenReservationShouldNotBeCreated() {
        Reservation reservation4 = new Reservation();
        reservation4.setId(5L);
        reservation4.setSeats(Arrays.asList("1A"));
        reservation4.setTrip(trip);
        reservation4.setPrice(30.0);
        reservation4.setUser(user);

        Mockito.when(reservationRepository.save(reservation4)).thenReturn(reservation4);

        Reservation saved = reservationService.createReservation(reservation4, null);

        assertThat(saved).isNull();
    }

    @Test
    void whenCreateInvalidReservation_thenReservationShouldNotBeCreated2() {
        Reservation reservation4 = new Reservation();
        reservation4.setId(5L);
        reservation4.setSeats(Arrays.asList("5A"));
        trip.setFreeSeats(0);
        reservation4.setTrip(trip);
        reservation4.setPrice(30.0);
        reservation4.setUser(user);

        Mockito.when(reservationRepository.save(reservation4)).thenReturn(reservation4);

        Reservation saved = reservationService.createReservation(reservation4, null);

        assertThat(saved).isNull();
    }

    @Test
    void whenUpdateCheckedIn_thenReservationShouldBeCheckedIn() {
        Reservation checkedIn = reservationService.updateCheckedIn(1L);

        assertThat(checkedIn).isNotNull();
        assertThat(checkedIn.isCheckedIn()).isTrue();
    }

    @Test
    void whenUpdateCheckedInInvalidReservation_thenShouldReturnNull() {
        Reservation checkedIn = reservationService.updateCheckedIn(12345L);

        assertThat(checkedIn).isNull();
    }
}
