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
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.entities.*;
import pt.ua.deti.tqs.backend.repositories.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        reservationRepository.deleteAll();
        tripRepository.deleteAll();
        busRepository.deleteAll();
        cityRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void whenValidInput_thenCreateNormalUser() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setRoles(List.of(UserRole.USER));

        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"username\":\"johndoe\",\"password\":\"password\",\"name\":\"John Doe\",\"email\":\"johndoe@ua.pt\"}")
                   .when().post(BASE_URL + "/api/public/user")
                   .then().statusCode(HttpStatus.CREATED.value())
                   .body("username", equalTo(user.getUsername()))
                   .body("name", equalTo(user.getName()))
                   .body("email", equalTo(user.getEmail()))
                   .body("roles", hasSize(1))
                   .body("roles[0]", is(UserRole.USER.toString()));

        List<User> found = repository.findAll();
        assertThat(found).extracting(User::getUsername).containsOnly(user.getUsername());
        assertThat(found).extracting(User::getName).containsOnly(user.getName());
        assertThat(found).extracting(User::getEmail).containsOnly(user.getEmail());
    }

    @Test
    void whenValidInput_thenCreateUser() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setRoles(List.of(UserRole.USER, UserRole.STAFF));

        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"username\":\"johndoe\",\"password\":\"password\",\"name\":\"John Doe\",\"email\":\"johndoe@ua.pt\",\"roles\":[\"USER\",\"STAFF\"]}")
                   .when().post(BASE_URL + "/api/backoffice/user")
                   .then().statusCode(HttpStatus.CREATED.value())
                   .body("username", equalTo(user.getUsername()))
                   .body("name", equalTo(user.getName()))
                   .body("email", equalTo(user.getEmail()))
                   .body("roles", hasSize(2))
                   .body("roles", hasItems(UserRole.USER.toString(), UserRole.STAFF.toString()));

        List<User> found = repository.findAll();
        assertThat(found).extracting(User::getUsername).containsOnly(user.getUsername());
        assertThat(found).extracting(User::getName).containsOnly(user.getName());
        assertThat(found).extracting(User::getEmail).containsOnly(user.getEmail());
    }

    @Test
    void whenGetUserById_thenStatus200() {
        User user = createTestUser("John Doe", "johndoe@ua.pt", "johndoe", "password", List.of(UserRole.USER));

        RestAssured.when().get(BASE_URL + "/api/public/user/" + user.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("name", equalTo(user.getName()))
                   .body("email", equalTo(user.getEmail()))
                   .body("username", equalTo(user.getUsername()));
    }

    @Test
    void whenGetUserByInvalidId_thenStatus404() {
        RestAssured.when().get(BASE_URL + "/api/public/user/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenGetUserByValidEmailAndPassword_thenGetUser() {
        User user = createTestUser("John Doe", "johndoe@ua.pt", "johndoe", "password", List.of(UserRole.USER));

        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"email\":\"johndoe@ua.pt\",\"password\":\"password\"}")
                   .when().post(BASE_URL + "/api/public/user/login")
                   .then().statusCode(200)
                   .body("name", is(user.getName()))
                   .body("email", is(user.getEmail()))
                   .body("username", is(user.getUsername()));
    }

    @Test
    void whenGetUserByInvalidEmailAndPassword_thenGetNull() {
        createTestUser("John Doe", "johndoe@ua.pt", "johndoe", "password", List.of(UserRole.USER));

        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"email\":\"johndoe@ua.pt\",\"password\":\"password123\"}")
                   .when().post(BASE_URL + "/api/public/user/login")
                   .then().statusCode(401);
    }

    @Test
    void whenGetUserReservationsByUserId_thenStatus200() {
        User user = createTestUser("Jane Doe", "janedoe@ua.pt", "janedoe", "password", List.of(UserRole.USER));
        Reservation reservation = createTestReservation(user);

        RestAssured.when().get(BASE_URL + "/api/public/user/" + user.getId() + "/reservations")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("id", hasItems(reservation.getId().intValue()));
    }

    @Test
    void whenGetUserReservationsByUserIdAndCurrencyEur_thenStatus200() {
        User user = createTestUser("Jane Doe", "janedoe@ua.pt", "janedoe", "password", List.of(UserRole.USER));
        Reservation reservation = createTestReservation(user);

        RestAssured.when().get(BASE_URL + "/api/public/user/" + user.getId() + "/reservations?currency=EUR")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("id", hasItems(reservation.getId().intValue()))
                   .body("price", hasItems((float) reservation.getPrice()));
    }

    @Test
    void whenGetUserReservationsByUserIdAndCurrencyUsd_thenStatus200() {
        User user = createTestUser("Jane Doe", "janedoe@ua.pt", "janedoe", "password", List.of(UserRole.USER));
        Reservation reservation = createTestReservation(user);

        RestAssured.when().get(BASE_URL + "/api/public/user/" + user.getId() + "/reservations?currency=USD")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("id", hasItems(reservation.getId().intValue()))
                   .body("price", not(hasItems(reservation.getPrice())));
    }

    @Test
    void whenUpdateUser_thenStatus200() {
        User user = createTestUser("John Doe", "joghndoe@ua.pt", "johndoe", "password",
                                   List.of(UserRole.USER, UserRole.STAFF));

        user.setName("Jane Doe");
        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"name\":\"Jane Doe\", \"email\":\"johndoe@ua.pt\", \"username\":\"johndoe\", \"password\":\"password\", \"roles\":[\"USER\",\"STAFF\"]}")
                   .when().put(BASE_URL + "/api/backoffice/user/" + user.getId())
                   .then().statusCode(HttpStatus.OK.value()).body("name", equalTo(user.getName()));

        User updated = repository.findById(user.getId()).orElse(null);
        assertThat(updated).isNotNull().extracting(User::getName).isEqualTo(user.getName());
    }

    @Test
    void whenUpdateNormalUser_thenStatus200() {
        User user = createTestUser("John Doe", "joghndoe@ua.pt", "johndoe", "password",
                                   List.of(UserRole.USER));

        user.setName("Jane Doe");
        RestAssured.given().contentType(ContentType.JSON)
                   .body("{\"name\":\"Jane Doe\", \"email\":\"johndoe@ua.pt\", \"username\":\"johndoe\", \"password\":\"password\"}")
                   .when().put(BASE_URL + "/api/backoffice/user/" + user.getId())
                   .then().statusCode(HttpStatus.OK.value()).body("name", equalTo(user.getName()));

        User updated = repository.findById(user.getId()).orElse(null);
        assertThat(updated).isNotNull().extracting(User::getName).isEqualTo(user.getName());
    }

    @Test
    void whenUpdateInvalidNormalUser_thenStatus404() {
        User user = createTestUser("John Doe", "johndoe@ua.pt", "johndoe", "password", List.of(UserRole.USER));

        RestAssured.given().contentType(ContentType.JSON).body(user)
                   .when().put(BASE_URL + "/api/public/user/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenUpdateInvalidUser_thenStatus404() {
        User user = createTestUser("John Doe", "johndoe@ua.pt", "johndoe", "password", List.of(UserRole.USER));

        RestAssured.given().contentType(ContentType.JSON).body(user)
                   .when().put(BASE_URL + "/api/backoffice/user/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenDeleteUser_thenStatus200() {
        User user = createTestUser("John Doe", "johndoe@ua.pt", "johndoe", "password", List.of(UserRole.USER));

        RestAssured.when().delete(BASE_URL + "/api/public/user/" + user.getId())
                   .then().statusCode(HttpStatus.OK.value());

        assertThat(repository.findById(user.getId())).isEmpty();
    }

    private User createTestUser(String name, String email, String username, String password, List<UserRole> roles) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(roles);
        return repository.saveAndFlush(user);
    }

    private Reservation createTestReservation(User user) {
        Bus bus = new Bus();
        bus.setCapacity(50);
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
        reservation.setSeats(1);

        return reservationRepository.saveAndFlush(reservation);
    }
}
