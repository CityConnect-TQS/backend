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
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.repositories.BusRepository;
import pt.ua.deti.tqs.backend.repositories.CityRepository;
import pt.ua.deti.tqs.backend.repositories.TripRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "trip.status.update.delay=10000" // Set the delay to 10000 milliseconds
})
class TripControllerTestIT {
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16")
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test");

    String BASE_URL;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TripRepository repository;

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
        repository.deleteAll();
    }

    @Test
    void whenValidInput_thenCreateTrip() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus.setCompany("FlixBus");
        bus = busRepository.saveAndFlush(bus);

        City city = new City();
        city.setName("Aveiro");
        city = cityRepository.saveAndFlush(city);

        Trip trip = new Trip();
        trip.setDeparture(city);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrival(city);
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setPrice(10.0);

        RestAssured.given().contentType(ContentType.JSON).body(trip)
                   .when().post(BASE_URL + "/api/backoffice/trip")
                   .then().statusCode(HttpStatus.CREATED.value())
                   .body("price", equalTo((float) trip.getPrice()))
                   .body("departure.name", equalTo(trip.getDeparture().getName()))
                   .body("arrival.name", equalTo(trip.getArrival().getName()))
                   .body("bus.capacity", equalTo(trip.getBus().getCapacity()))
                   .body("departureTime",
                         equalTo(trip.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", equalTo(trip.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        List<Trip> found = repository.findAll();
        assertThat(found).extracting(Trip::getPrice).containsOnly(trip.getPrice());
        assertThat(found).extracting(Trip::getDeparture).extracting(City::getName)
                         .containsOnly(trip.getDeparture().getName());
        assertThat(found).extracting(Trip::getArrival).extracting(City::getName)
                         .containsOnly(trip.getArrival().getName());
        assertThat(found).extracting(Trip::getBus).extracting(Bus::getCapacity)
                         .containsOnly(trip.getBus().getCapacity());
        assertThat(found).extracting(Trip::getDepartureTime).containsOnly(trip.getDepartureTime());
        assertThat(found).extracting(Trip::getArrivalTime).containsOnly(trip.getArrivalTime());
        assertThat(found).extracting(Trip::getPrice).containsOnly(trip.getPrice());
    }

    @Test
    void givenTrips_whenGetTrips_thenStatus200() {
        Trip trip1 = createTestTrip();
        Trip trip2 = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(2))
                   .body("price", hasItems((float) trip1.getPrice(), (float) trip2.getPrice()))
                   .body("departure.name", hasItems(trip1.getDeparture().getName(), trip2.getDeparture().getName()))
                   .body("arrival.name", hasItems(trip1.getArrival().getName(), trip2.getArrival().getName()))
                   .body("bus.capacity", hasItems(trip1.getBus().getCapacity(), trip2.getBus().getCapacity()))
                   .body("departureTime",
                         hasItems(trip1.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                  trip2.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", hasItems(trip1.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                                 trip2.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("status", hasItems("ONTIME", "ONTIME"));
    }

    @Test
    void givenTrips_whenGetTripsWithCurrencyUsd_thenStatus200() {
        Trip trip1 = createTestTrip();
        Trip trip2 = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip?currency=USD")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(2))
                   .body("price", not(hasItems((float) trip1.getPrice(), (float) trip2.getPrice())));
    }

    @Test
    void givenTrips_whenGetTripsWithCurrencyEur_thenStatus200() {
        Trip trip1 = createTestTrip();
        Trip trip2 = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip?currency=EUR")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(2))
                   .body("price", hasItems((float) trip1.getPrice(), (float) trip2.getPrice()));
    }

    @Test
    void givenTrips_whenGetTripsByDeparture_thenStatus200() {
        Trip trip1 = createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=" + trip1.getDeparture().getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("price", hasItems((float) trip1.getPrice()))
                   .body("departure.name", hasItems(trip1.getDeparture().getName()))
                   .body("arrival.name", hasItems(trip1.getArrival().getName()))
                   .body("bus.capacity", hasItems(trip1.getBus().getCapacity()))
                   .body("departureTime",
                         hasItems(trip1.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", hasItems(trip1.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void givenTrips_whenGetTripsByInvalidDeparture_thenStatus200() {
        createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=999")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void givenTrips_whenGetTripsByArrival_thenStatus200() {
        Trip trip1 = createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?arrival=" + trip1.getArrival().getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("price", hasItems((float) trip1.getPrice()))
                   .body("departure.name", hasItems(trip1.getDeparture().getName()))
                   .body("arrival.name", hasItems(trip1.getArrival().getName()))
                   .body("bus.capacity", hasItems(trip1.getBus().getCapacity()))
                   .body("departureTime",
                         hasItems(trip1.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", hasItems(trip1.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        RestAssured.when().get(BASE_URL + "/api/public/trip?arrival=999")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void givenTrips_whenGetTripsByInvalidArrival_thenStatus200() {
        createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?arrival=999")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void givenTrips_whenGetTripsByDepartureAndArrival_thenStatus200() {
        Trip trip1 = createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");
        createTestTrip("Aveiro", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=" + trip1.getDeparture().getId()
                                       + "&arrival=" + trip1.getArrival().getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("price", hasItems((float) trip1.getPrice()))
                   .body("departure.name", hasItems(trip1.getDeparture().getName()))
                   .body("arrival.name", hasItems(trip1.getArrival().getName()))
                   .body("bus.capacity", hasItems(trip1.getBus().getCapacity()))
                   .body("departureTime",
                         hasItems(trip1.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", hasItems(trip1.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=999&arrival=999")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void givenTrips_whenGetTripsByInvalidDepartureAndArrival_thenStatus200() {
        createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");
        createTestTrip("Aveiro", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=999&arrival=999")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void givenTrips_whenGetTripsByDepartureAndArrivalAndDepartureTimeAfter_thenStatus200() {
        Trip trip1 = createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");
        createTestTrip("Aveiro", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=" + trip1.getDeparture().getId()
                                       + "&arrival=" + trip1.getArrival().getId()
                                       + "&departureTime=" + trip1.getDepartureTime().minusHours(1))
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("price", hasItems((float) trip1.getPrice()))
                   .body("departure.name", hasItems(trip1.getDeparture().getName()))
                   .body("arrival.name", hasItems(trip1.getArrival().getName()))
                   .body("bus.capacity", hasItems(trip1.getBus().getCapacity()))
                   .body("departureTime",
                         hasItems(trip1.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", hasItems(trip1.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void givenTrips_whenGetTripsByInvalidDepartureAndArrivalAndDepartureTimeAfter_thenStatus200() {
        createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");
        createTestTrip("Aveiro", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=999&arrival=999&departureTime="
                                       + LocalDateTime.now().plusHours(1))
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void givenTrips_whenGetTripsByDepartureAndArrivalAndDepartureTimeAfterAndSeats_thenStatus200() {
        Trip trip1 = createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");
        createTestTrip("Aveiro", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=" + trip1.getDeparture().getId()
                                       + "&arrival=" + trip1.getArrival().getId()
                                       + "&departureTime=" + trip1.getDepartureTime().minusHours(1)
                                       + "&seats=20")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("price", hasItems((float) trip1.getPrice()))
                   .body("departure.name", hasItems(trip1.getDeparture().getName()))
                   .body("arrival.name", hasItems(trip1.getArrival().getName()))
                   .body("bus.capacity", hasItems(trip1.getBus().getCapacity()))
                   .body("departureTime",
                         hasItems(trip1.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", hasItems(trip1.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void givenTrips_whenGetTripsByInvalidDepartureAndArrivalAndDepartureTimeAfterAndSeats_thenStatus200() {
        createTestTrip("Aveiro", "Porto");
        createTestTrip("Porto", "Lisboa");
        createTestTrip("Aveiro", "Lisboa");

        RestAssured.when().get(BASE_URL + "/api/public/trip?departure=999&arrival=999&departureTime="
                                       + LocalDateTime.now().plusHours(1) + "&seats=999")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void whenGetTripById_thenStatus200() {
        Trip trip = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip/" + trip.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) trip.getPrice()))
                   .body("departure.name", equalTo(trip.getDeparture().getName()))
                   .body("arrival.name", equalTo(trip.getArrival().getName()))
                   .body("bus.capacity", equalTo(trip.getBus().getCapacity()))
                   .body("departureTime",
                         equalTo(trip.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                   .body("arrivalTime", equalTo(trip.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void whenGetTripByIdWithCurrencyUsd_thenStatus200() {
        Trip trip = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip/" + trip.getId() + "?currency=USD")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", not(equalTo((float) trip.getPrice())));
    }

    @Test
    void whenGetTripByIdWithCurrencyEur_thenStatus200() {
        Trip trip = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip/" + trip.getId() + "?currency=EUR")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) trip.getPrice()));
    }

    @Test
    void whenGetTripByInvalidId_thenStatus404() {
        RestAssured.when().get(BASE_URL + "/api/public/trip/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenGetReservationsByTripIdAndNoTripsFound_thenStatus404() {
        Trip trip = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip/" + trip.getId() + "/reservations")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenUpdateTrip_thenStatus200() {
        Trip trip = createTestTrip();

        trip.setPrice(20.0);
        RestAssured.given().contentType(ContentType.JSON).body(trip)
                   .when().put(BASE_URL + "/api/backoffice/trip/" + trip.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("price", equalTo((float) trip.getPrice()));

        Trip updatedTrip = repository.findById(trip.getId()).orElse(null);
        assertThat(updatedTrip).isNotNull();
        assertThat(updatedTrip.getPrice()).isEqualTo(20.0);
    }

    @Test
    void whenUpdateTripWithInvalidId_thenStatus404() {
        Trip trip = createTestTrip();

        trip.setPrice(20.0);
        RestAssured.given().contentType(ContentType.JSON).body(trip)
                   .when().put(BASE_URL + "/api/backoffice/trip/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenDeleteTrip_thenStatus200() {
        Trip trip = createTestTrip();

        RestAssured.when().delete(BASE_URL + "/api/backoffice/trip/" + trip.getId())
                   .then().statusCode(HttpStatus.OK.value());

        assertThat(repository.findById(trip.getId())).isEmpty();
    }

    @Test
    void whenGetAllTripsOnBackoffice_thenStatus200() {
        createTestTrip();
        createTestTrip();
        createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/backoffice/trip")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(3));
    }

    @Test
    void givenOnTimeTrip_whenTheOnboardingTimeArrives_thenStatusChanges() throws InterruptedException{
        Trip trip = createTestTrip();

        RestAssured.when().get(BASE_URL + "/api/public/trip/" + trip.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("status", equalTo("ONTIME"));

        Thread.sleep(10000);

        RestAssured.when().get(BASE_URL + "/api/public/trip/" + trip.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("status", equalTo("ONBOARDING"));
    }

    private Trip createTestTrip() {
        return createTestTrip("Aveiro", "Aveiro");
    }

    private Trip createTestTrip(String departure, String arrival) {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus.setCompany("FlixBus");
        bus = busRepository.saveAndFlush(bus);

        City city = new City();
        city.setName(departure);
        city = cityRepository.saveAndFlush(city);

        City city2 = new City();
        city2.setName(arrival);
        city2 = cityRepository.saveAndFlush(city2);

        Trip trip = new Trip();
        trip.setDeparture(city);
        trip.setDepartureTime(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.SECONDS));
        trip.setArrival(city2);
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setPrice(10.0);
        trip.setFreeSeats(50);
        return repository.saveAndFlush(trip);
    }
}
