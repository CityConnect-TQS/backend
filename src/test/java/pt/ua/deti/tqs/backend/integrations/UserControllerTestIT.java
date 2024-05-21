package pt.ua.deti.tqs.backend.integrations;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.entities.*;
import pt.ua.deti.tqs.backend.repositories.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.qos.logback.core.testUtil.RandomUtil.getRandomServerPort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"trip.status.update.delay=1000"})
@Testcontainers
class UserControllerTestIT {
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16")
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test");

    String BASE_URL;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private CityRepository cityRepository;

    private String jwtToken;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @BeforeEach
    public void setUp() {
        BASE_URL = "http://localhost:" + randomServerPort;
    }

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
        reservationRepository.deleteAll();
        tripRepository.deleteAll();
        busRepository.deleteAll();
        cityRepository.deleteAll();
    }

    @Test
    void whenValidInput_thenPostUserAndVerifyToken() {
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

        List<User> found = repository.findAll();
        assertThat(found).extracting(User::getName).containsOnly("name");
        assertThat(found).extracting(User::getEmail).containsOnly("email");
        assertThat(jwtToken).isNotNull();
    }

    @Test
    void whenValidInput_thenCreateUser() {
        String body = "{\"password\":\"" + "password" +
                "\",\"name\":\"" + "name" +
                "\",\"email\":\"" + "email" +
                "\",\"roles\":[\"USER\",\"STAFF\"]}";

        RestAssured.given().contentType(ContentType.JSON)
                .body(body)
                .when().post(BASE_URL + "/api/backoffice/user")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo("name"))
                .body("email", equalTo("email"))
                .body("roles", hasSize(2))
                .body("roles", hasItems(UserRole.USER.toString(), UserRole.STAFF.toString()));

        List<User> found = repository.findAll();
        assertThat(found).extracting(User::getName).containsOnly("name");
        assertThat(found).extracting(User::getEmail).containsOnly("email");
    }

    @Test
    void whenGetUserById_thenStatus200() {
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

        User user = repository.findAll().get(0);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/user/" + user.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("name", equalTo(user.getName()))
                   .body("email", equalTo(user.getEmail()))
                   .body("roles", hasSize(2))
                   .body("roles", hasItems(UserRole.USER.toString(), UserRole.STAFF.toString()));
    }

    @Test
    void whenGetUserByInvalidId_thenStatus404() {
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

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/user/999")
                   .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void whenGetUserByValidEmailAndPassword_thenGetUser() {
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

        User user = repository.findAll().get(0);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                   .body("{\"email\":\"email\",\"password\":\"password\"}")
                   .when().post(BASE_URL + "/api/public/user/login")
                   .then().statusCode(200)
                   .body("name", is(user.getName()))
                   .body("email", is(user.getEmail()));
    }

    @Test
    void whenGetUserByInvalidEmailAndPassword_thenGetNull() {
        createTestUser("John Doe", "johndoe@ua.pt", "password", List.of(UserRole.USER));

        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"email\":\"johndoe@ua.pt\",\"password\":\"password123\"}")
                   .when().post(BASE_URL + "/api/public/user/login")
                   .then().statusCode(401);
    }

    @Test
    void whenGetUserReservationsByUserId_thenStatus200() {
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

        User user = repository.findAll().get(0);

        Reservation reservation = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/user/" + user.getId() + "/reservations")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("id", hasItems(reservation.getId().intValue()));
    }

    @Test
    void whenGetUserReservationsByUserIdAndCurrencyEur_thenStatus200() {
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

        User user = repository.findAll().get(0);
        Reservation reservation = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/user/" + user.getId() + "/reservations?currency=EUR")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("id", hasItems(reservation.getId().intValue()))
                   .body("price", hasItems((float) reservation.getPrice()));
    }

    @Test
    void whenGetUserReservationsByUserIdAndCurrencyUsd_thenStatus200() {
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

        User user = repository.findAll().get(0);
        Reservation reservation = createTestReservation(user);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).when().get(BASE_URL + "/api/public/user/" + user.getId() + "/reservations?currency=USD")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("id", hasItems(reservation.getId().intValue()))
                   .body("price", not(hasItems(reservation.getPrice())));
    }

    @Test
    void whenUpdateUser_thenStatus200() {
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

        User user = repository.findAll().get(0);
        user.setName("Jane Doe");

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body("{\"name\":\"Jane Doe\", \"email\":\"email\", \"password\":\"password\", \"roles\":[\"USER\",\"STAFF\"]}")
                .when().put(BASE_URL + "/api/backoffice/user/" + user.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body("name", equalTo(user.getName()));

        User updated = repository.findById(user.getId()).orElse(null);
        assertThat(updated).isNotNull().extracting(User::getName).isEqualTo(user.getName());
    }

    @Test
    void whenUpdateNormalUser_thenStatus200() {
        String body = "{\"password\":\"" + "password" +
                "\",\"name\":\"" + "name" +
                "\",\"email\":\"" + "email" +
                "\",\"roles\":[\"USER\"]}";

        jwtToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(BASE_URL + "/api/public/user")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().jsonPath().getString("token");

        User user = repository.findAll().get(0);
        user.setName("Jane Doe");

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body("{\"name\":\"Jane Doe\", \"email\":\"email\", \"password\":\"password\", \"roles\":[\"USER\"]}")
                .when().put(BASE_URL + "/api/public/user/" + user.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body("name", equalTo(user.getName()));

        User updated = repository.findById(user.getId()).orElse(null);
        assertThat(updated).isNotNull().extracting(User::getName).isEqualTo(user.getName());
    }

    @Test
    void whenUpdateInvalidNormalUser_thenStatus404() {
        String body = "{\"password\":\"" + "password" +
                "\",\"name\":\"" + "name" +
                "\",\"email\":\"" + "email" +
                "\",\"roles\":[\"USER\"]}";

        jwtToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(BASE_URL + "/api/public/user")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().jsonPath().getString("token");

        User user = repository.findAll().get(0);

        RestAssured.given().contentType(ContentType.JSON)
                .body("{\"name\":\"Jane Doe\", \"email\":\"email\", \"password\":\"password\", \"roles\":[\"USER\"]}")
                .when().put(BASE_URL + "/api/backoffice/user/" + user.getId())
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void whenUpdateInvalidUser_thenStatus404() {
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

        User user = repository.findAll().get(0);

        RestAssured.given().contentType(ContentType.JSON)
                .body("{\"name\":\"Jane Doe\", \"email\":\"email\", \"password\":\"password\", \"roles\":[\"USER\"]}")
                .when().put(BASE_URL + "/api/backoffice/user/" + user.getId())
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void whenDeleteUser_thenStatus404() {
        String body = "{\"password\":\"" + "password" +
                "\",\"name\":\"" + "name" +
                "\",\"email\":\"" + "email" +
                "\",\"roles\":[\"USER\"]}";

        jwtToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(BASE_URL + "/api/backoffice/user")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().jsonPath().getString("token");

        User user = repository.findAll().get(0);

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when().delete(BASE_URL + "/api/backoffice/user/" + user.getId())
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    private User createTestUser(String name, String email, String password, List<UserRole> roles) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(roles);
        return repository.saveAndFlush(user);
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
        trip = tripRepository.saveAndFlush(trip);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTrip(trip);
        reservation.setPrice(10.0);
        reservation.setSeats(Arrays.asList("1A", "1B"));

        return reservationRepository.saveAndFlush(reservation);
    }
}
