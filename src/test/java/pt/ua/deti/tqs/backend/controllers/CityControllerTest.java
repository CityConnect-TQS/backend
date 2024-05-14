package pt.ua.deti.tqs.backend.controllers;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.components.JwtUtils;
import pt.ua.deti.tqs.backend.controllers.backoffice.CityBackofficeController;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.services.CityService;
import pt.ua.deti.tqs.backend.services.CustomUserDetailsService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@WebMvcTest({CityBackofficeController.class, CityController.class})
@AutoConfigureMockMvc(addFilters = false)
class CityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService userService;

    @Test
    void whenPostCity_thenCreateCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Aveiro");

        when(service.createCity(Mockito.any())).thenReturn(city);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(city)
                          .when().post("/api/backoffice/city")
                          .then().statusCode(HttpStatus.CREATED.value())
                          .body("name", is("Aveiro"));

        verify(service, times(1)).createCity(Mockito.any());
    }

    @Test
    void givenManyCities_whenGetCities_thenReturnJsonArray() {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Aveiro");
        City city2 = new City();
        city2.setId(2L);
        city2.setName("Porto");
        City city3 = new City();
        city3.setId(3L);
        city3.setName("Lisboa");

        when(service.getAllCities()).thenReturn(Arrays.asList(city1, city2, city3));

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/city")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("$", hasSize(3))
                          .body("[0].name", is("Aveiro"))
                          .body("[1].name", is("Porto"))
                          .body("[2].name", is("Lisboa"));

        verify(service, times(1)).getAllCities();
    }

    @Test
    void whenGetCityById_thenReturnCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Aveiro");

        when(service.getCity(1L)).thenReturn(city);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/city/1")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("name", is("Aveiro"));

        verify(service, times(1)).getCity(1L);
    }

    @Test
    void whenGetCityByName_thenReturnCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Aveiro");

        when(service.getCitiesByName("Aveiro")).thenReturn(List.of(city));

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/city?name=Aveiro")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("[0].name", is("Aveiro"));

        verify(service, times(1)).getCitiesByName("Aveiro");
    }

    @Test
    void whenGetCityByInvalidId_thenReturnNotFound() {
        when(service.getCity(1L)).thenReturn(null);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/city/1")
                          .then().statusCode(HttpStatus.NOT_FOUND.value());

        verify(service, times(1)).getCity(1L);
    }

    @Test
    void whenUpdateCity_thenUpdateCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Aveiro");

        when(service.updateCity(Mockito.any(City.class))).then(returnsFirstArg());

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(city)
                          .when().put("/api/backoffice/city/1")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("name", is("Aveiro"));

        verify(service, times(1)).updateCity(Mockito.any());
    }

    @Test
    void whenDeleteCity_thenDeleteCity() {
        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().delete("/api/backoffice/city/1")
                          .then().statusCode(HttpStatus.OK.value());

        verify(service, times(1)).deleteCity(1L);
    }
}
