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
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.repositories.CityRepository;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"trip.status.update.delay=1000"})
@Testcontainers
class CityControllerTestIT {
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16")
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test");

    String BASE_URL;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private CityRepository repository;

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
        userRepository.deleteAll();
    }

    @Test
    void whenValidInput_thenCreateCity() {
        City city = new City();
        city.setName("Aveiro");

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(city)
                .when().post(BASE_URL + "/api/backoffice/city")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(city.getName()));

        List<City> found = repository.findAll();
        assertThat(found).extracting(City::getName).containsOnly(city.getName());
    }

    @Test
    void givenCities_whenGetCities_thenStatus200() {
        City city = createTestCity("Aveiro");
        City city2 = createTestCity("Porto");

        RestAssured.when().get(BASE_URL + "/api/public/city")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(2))
                   .body("name", hasItems(city.getName(), city2.getName()));
    }

    @Test
    void whenGetCityById_thenStatus200() {
        City city = createTestCity("Aveiro");

        RestAssured.when().get(BASE_URL + "/api/public/city/" + city.getId())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("name", equalTo(city.getName()));
    }

    @Test
    void whenGetCityByName_thenStatus200() {
        City city = createTestCity("Aveiro");
        createTestCity("Porto");

        RestAssured.when().get(BASE_URL + "/api/public/city?name=" + city.getName())
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(1))
                   .body("name", hasItems(city.getName()));
    }

    @Test
    void whenGetCityByInvalidId_thenStatus404() {
        RestAssured.when().get(BASE_URL + "/api/public/city/999")
                   .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenGetCityByInvalidName_thenStatus404() {
        RestAssured.when().get(BASE_URL + "/api/public/city?name=aaaa")
                   .then().statusCode(HttpStatus.OK.value())
                   .body("", hasSize(0));
    }

    @Test
    void whenUpdateCity_thenStatus200() {
        City city = createTestCity("Aveiro");

        city.setName("Porto");
        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).body(city)
                .when().put(BASE_URL + "/api/backoffice/city/" + city.getId())
                .then().statusCode(HttpStatus.OK.value()).body("name", equalTo(city.getName()));
    }

    @Test
    void whenUpdateInvalidCity_thenStatus404() {
        City city = createTestCity("Aveiro");

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken).body(city)
                .when().put(BASE_URL + "/api/backoffice/city/999")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void whenDeleteCity_thenStatus200() {
        City city = createTestCity("Aveiro");

        RestAssured.given().header("Authorization", "Bearer " + jwtToken).when().delete(BASE_URL + "/api/backoffice/city/" + city.getId())
                   .then().statusCode(HttpStatus.OK.value());

        assertThat(repository.findById(city.getId())).isEmpty();
    }

    private City createTestCity(String name) {
        City city = new City();
        city.setName(name);
        repository.saveAndFlush(city);
        return city;
    }
}
