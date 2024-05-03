package pt.ua.deti.tqs.backend.integrations;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.deti.tqs.backend.entities.*;
import pt.ua.deti.tqs.backend.repositories.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ReservationControllerTestIT {
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16")
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test");

    String BASE_URL;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @BeforeEach
    void setBASE_URL() {
        BASE_URL = "http://localhost:" + randomServerPort;
    }

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
        tripRepository.deleteAll();
        busRepository.deleteAll();
        cityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenValidInput_thenCreateReservation() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus = busRepository.saveAndFlush(bus);

        City city = new City();
        city.setName("Aveiro");
        city = cityRepository.saveAndFlush(city);

        User user = new User();
        user.setEmail("user@ua.pt");
        user.setName("User");
        user.setPassword("password");
        user = userRepository.saveAndFlush(user);

        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setPrice(10.0);
        trip.setDeparture(city);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrival(city);
        trip.setArrivalTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        trip.calculateFreeSeats();
        trip = tripRepository.saveAndFlush(trip);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTrip(trip);
        reservation.setPrice(10.0);
        reservation.setSeats(1);

        RestAssured.given().contentType(ContentType.JSON).body(reservation)
                   .when().post(BASE_URL + "/api/public/reservation")
                   .then().statusCode(HttpStatus.CREATED.value())
                   .body("price", equalTo((float) reservation.getPrice()))
                   .body("seats", equalTo(reservation.getSeats()))
                   .body("trip.price", equalTo((float) reservation.getTrip().getPrice()))
                   .body("trip.departure.name", equalTo(reservation.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", equalTo(reservation.getTrip().getArrival().getName()))
                   .body("trip.departureTime", equalTo(reservation.getTrip().getDepartureTime()
                                                                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("trip.arrivalTime",
                         equalTo(reservation.getTrip().getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void whenValidInputAndSeatsGreaterThanCapacity_thenBadRequest() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus = busRepository.saveAndFlush(bus);

        City city = new City();
        city.setName("Aveiro");
        city = cityRepository.saveAndFlush(city);

        User user = new User();
        user.setEmail("user@ua.pt");
        user.setName("User");
        user.setPassword("password");
        user = userRepository.saveAndFlush(user);

        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setPrice(10.0);
        trip.setDeparture(city);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrival(city);
        trip.setArrivalTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        trip.calculateFreeSeats();
        trip = tripRepository.saveAndFlush(trip);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTrip(trip);
        reservation.setPrice(10.0);
        reservation.setSeats(51);

        RestAssured.given().contentType(ContentType.JSON).body(reservation)
                   .when().post(BASE_URL + "/api/public/reservation")
                   .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenReservations_whenGetReservations_thenStatus200() {
        Reservation reservation1 = createTestReservation();
        Reservation reservation2 = createTestReservation();

        RestAssured.when().get(BASE_URL + "/api/backoffice/reservation")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(2))
                   .body("price", hasItems((float) reservation1.getPrice(), (float) reservation2.getPrice()))
                   .body("seats", hasItems(reservation1.getSeats(), reservation2.getSeats()))
                   .body("trip.price",
                         hasItems((float) reservation1.getTrip().getPrice(), (float) reservation2.getTrip().getPrice()))
                   .body("trip.departure.name", hasItems(reservation1.getTrip().getDeparture().getName(),
                                                         reservation2.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", hasItems(reservation1.getTrip().getArrival().getName(),
                                                       reservation2.getTrip().getArrival().getName()))
                   .body("trip.departureTime", hasItems(
                           reservation1.getTrip().getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                           reservation2.getTrip().getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("trip.arrivalTime",
                         hasItems(reservation1.getTrip().getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                  reservation2.getTrip().getArrivalTime()
                                              .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void whenGetReservationById_thenStatus200() {
        Reservation reservation = createTestReservation();

        RestAssured.when().get(BASE_URL + "/api/public/reservation/" + reservation.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) reservation.getPrice()))
                   .body("seats", equalTo(reservation.getSeats()))
                   .body("trip.price", equalTo((float) reservation.getTrip().getPrice()))
                   .body("trip.departure.name", equalTo(reservation.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", equalTo(reservation.getTrip().getArrival().getName()))
                   .body("trip.departureTime", equalTo(reservation.getTrip().getDepartureTime()
                                                                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("trip.arrivalTime",
                         equalTo(reservation.getTrip().getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void whenGetReservationByIdAndCurrencyEuro_thenStatus200() {
        Reservation reservation = createTestReservation();

        RestAssured.when().get(BASE_URL + "/api/public/reservation/" + reservation.getId() + "?currency=EUR")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) reservation.getPrice()))
                   .body("seats", equalTo(reservation.getSeats()))
                   .body("trip.price", equalTo((float) reservation.getTrip().getPrice()))
                   .body("trip.departure.name", equalTo(reservation.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", equalTo(reservation.getTrip().getArrival().getName()))
                   .body("trip.departureTime", equalTo(reservation.getTrip().getDepartureTime()
                                                                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("trip.arrivalTime",
                         equalTo(reservation.getTrip().getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void whenGetReservationByIdAndCurrencyUsd_thenStatus200() {
        Reservation reservation = createTestReservation();

        RestAssured.when().get(BASE_URL + "/api/public/reservation/" + reservation.getId() + "?currency=USD")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", not(equalTo((float) reservation.getPrice())))
                   .body("seats", equalTo(reservation.getSeats()))
                   .body("trip.price", not(equalTo((float) reservation.getTrip().getPrice())))
                   .body("trip.departure.name", equalTo(reservation.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", equalTo(reservation.getTrip().getArrival().getName()))
                   .body("trip.departureTime", equalTo(reservation.getTrip().getDepartureTime()
                                                                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("trip.arrivalTime",
                         equalTo(reservation.getTrip().getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void whenGetReservationByInvalidId_thenStatus404() {
        RestAssured.when().get(BASE_URL + "/api/public/reservation/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenUpdateReservation_thenStatus200() {
        Reservation reservation = createTestReservation();
        reservation.setSeats(2);
        reservation.setPrice(20.0);

        RestAssured.given().contentType(ContentType.JSON).body(reservation)
                   .when().put(BASE_URL + "/api/backoffice/reservation/" + reservation.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) reservation.getPrice()))
                   .body("seats", equalTo(reservation.getSeats()));

        Reservation updated = repository.findById(reservation.getId()).orElse(null);
        assertThat(updated).isNotNull().extracting(Reservation::getSeats, Reservation::getPrice)
                           .containsExactly(2, 20.0);
    }

    @Test
    void whenUpdateInvalidReservation_thenStatus404() {
        Reservation reservation = createTestReservation();

        RestAssured.given().contentType(ContentType.JSON).body(reservation)
                   .when().put(BASE_URL + "/api/backoffice/reservation/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenDeleteReservation_thenStatus200() {
        Reservation reservation = createTestReservation();

        RestAssured.when().delete(BASE_URL + "/api/public/reservation/" + reservation.getId())
                   .then().statusCode(HttpStatus.OK.value());

        Reservation found = repository.findById(reservation.getId()).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void whenDeleteReservationWithInvalidId_thenStatus200() {
        // This assures the trip != null conditionl
        RestAssured.when().delete(BASE_URL + "/api/public/reservation/999")
                   .then().statusCode(HttpStatus.OK.value());

        Reservation found = repository.findById(999L).orElse(null);
        assertThat(found).isNull();
    }

    private Reservation createTestReservation() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus = busRepository.saveAndFlush(bus);

        City city = new City();
        city.setName("Aveiro");
        city = cityRepository.saveAndFlush(city);

        User user = new User();
        user.setEmail("user@ua.pt");
        user.setName("User");
        user.setPassword("password");
        user = userRepository.saveAndFlush(user);

        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setPrice(10.0);
        trip.setDeparture(city);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrival(city);
        trip.setArrivalTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        trip = tripRepository.saveAndFlush(trip);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTrip(trip);
        reservation.setPrice(10.0);
        reservation.setSeats(1);

        return repository.saveAndFlush(reservation);
    }
}
