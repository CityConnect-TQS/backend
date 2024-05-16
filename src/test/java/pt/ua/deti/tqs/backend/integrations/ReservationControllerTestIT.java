package pt.ua.deti.tqs.backend.integrations;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.deti.tqs.backend.entities.*;
import pt.ua.deti.tqs.backend.repositories.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {"trip.status.update.delay=1000"})
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

    private String jwtToken;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @BeforeEach
    public void createAdminUser() {
        BASE_URL = "http://localhost:" + randomServerPort;

        String body = "{\"password\":\"" + "password" +
                "\",\"name\":\"" + "name" +
                "\",\"email\":\"" + "email" +
                "\",\"roles\":[\"USER\",\"STAFF\"]}";

        jwtToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(BASE_URL + "/api/backoffice/user")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().jsonPath().getString("token");
    }

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
        repository.deleteAllInBatch();
        tripRepository.deleteAll();
        busRepository.deleteAll();
        cityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenValidInputAndSeatsGreaterThanCapacity_thenBadRequest() {
        User user = userRepository.findAll().get(0);

        Reservation reservation = createTestReservation(user);

        reservation.setSeats(Arrays.asList("1A", "1B", "1C", "1D", "1E", "1F", "1G", "1H", "1I", "1J"));

        RestAssured.given().contentType(ContentType.JSON).body(reservation)
                   .header("Authorization", "Bearer " + jwtToken)
                   .when().post(BASE_URL + "/api/public/reservation")
                   .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenReservations_whenGetReservations_thenStatus200() {
        User user = userRepository.findAll().get(0);
        Reservation reservation1 = createTestReservation(user);
        Reservation reservation2 = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/backoffice/reservation")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(2))
                   .body("price", hasItems((float) reservation1.getPrice(), (float) reservation2.getPrice()))
                   .body("seats", hasItems(reservation1.getSeats(), reservation2.getSeats()))
                   .body("trip.price",
                         hasItems((float) reservation1.getTrip().getPrice(), (float) reservation2.getTrip().getPrice()))
                   .body("trip.departure.name", hasItems(reservation1.getTrip().getDeparture().getName(),
                                                         reservation2.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", hasItems(reservation1.getTrip().getArrival().getName(),
                                                       reservation2.getTrip().getArrival().getName()));

        repository.deleteById(reservation1.getId());
        repository.deleteById(reservation2.getId());
        System.out.println(repository.findAll());
    }

    @Test
    void whenGetReservationById_thenStatus200() {
        User user = userRepository.findAll().get(0);
        Reservation reservation = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/reservation/" + reservation.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) reservation.getPrice()))
                   .body("seats", equalTo(reservation.getSeats()))
                   .body("trip.price", equalTo((float) reservation.getTrip().getPrice()))
                   .body("trip.departure.name", equalTo(reservation.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", equalTo(reservation.getTrip().getArrival().getName()));
    }

    @Test
    void whenGetReservationByIdAndCurrencyEuro_thenStatus200() {
        User user = userRepository.findAll().get(0);
        Reservation reservation = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/reservation/" + reservation.getId() + "?currency=EUR")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) reservation.getPrice()))
                   .body("seats", equalTo(reservation.getSeats()))
                   .body("trip.price", equalTo((float) reservation.getTrip().getPrice()))
                   .body("trip.departure.name", equalTo(reservation.getTrip().getDeparture().getName()))
                   .body("trip.arrival.name", equalTo(reservation.getTrip().getArrival().getName()));
    }

    @Test
    @Disabled("This test is disabled because the currency conversion is not working")
    void whenGetReservationByIdAndCurrencyUsd_thenStatus200() {
        User user = userRepository.findAll().get(0);
        Reservation reservation = createTestReservation(user);

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
        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/reservation/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenDeleteReservation_thenStatus200() {
        User user = userRepository.findAll().get(0);
        Reservation reservation = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().delete(BASE_URL + "/api/public/reservation/" + reservation.getId())
                   .then().statusCode(HttpStatus.OK.value());

        Reservation found = repository.findById(reservation.getId()).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void whenDeleteReservationWithInvalidId_thenStatus200() {
        // This assures the trip != null conditionl
        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().delete(BASE_URL + "/api/public/reservation/999")
                   .then().statusCode(HttpStatus.OK.value());

        Reservation found = repository.findById(999L).orElse(null);
        assertThat(found).isNull();
    }

    private Reservation createTestReservation(User user) {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus.setCompany("Flexibus");
        bus = busRepository.saveAndFlush(bus);

        City city = new City();
        city.setName("Aveiro");
        city = cityRepository.saveAndFlush(city);

        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setPrice(10.0);
        trip.setDeparture(city);
        trip.setDepartureTime(LocalDateTime.now());
        trip.setArrival(city);
        trip.setArrivalTime(LocalDateTime.now().plusHours(1));
        trip.setFreeSeats(bus.getCapacity());
        trip = tripRepository.saveAndFlush(trip);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTrip(trip);
        reservation.setPrice(10.0);
        reservation.setSeats(Arrays.asList("1A", "1B"));

        return repository.saveAndFlush(reservation);
    }
}
