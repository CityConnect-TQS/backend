package pt.ua.deti.tqs.backend.controllers;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.controllers.backoffice.BusController;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.services.BusService;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@WebMvcTest(BusController.class)
class BusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusService service;

    @Test
    void whenPostBus_thenCreateBus() {
        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        when(service.createBus(Mockito.any())).thenReturn(bus);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(ContentType.JSON).body(bus)
                          .when().post("/api/backoffice/bus")
                          .then().statusCode(HttpStatus.CREATED.value())
                          .body("capacity", is(50))
                          .body("company", is("Flexibus"));

        verify(service, times(1)).createBus(Mockito.any());
    }

    @Test
    void givenManyBuses_whenGetBuses_thenReturnJsonArray() {
        Bus bus1 = new Bus();
        bus1.setId(1L);
        bus1.setCapacity(50);
        bus1.setCompany("Flexibus");
        Bus bus2 = new Bus();
        bus2.setId(2L);
        bus2.setCapacity(60);
        bus2.setCompany("Transdev");
        Bus bus3 = new Bus();
        bus3.setId(3L);
        bus3.setCapacity(70);
        bus3.setCompany("Flexibus");

        when(service.getAllBuses()).thenReturn(Arrays.asList(bus1, bus2, bus3));

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/bus")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("$", hasSize(3))
                          .body("[0].capacity", is(50))
                          .body("[1].capacity", is(60))
                          .body("[2].capacity", is(70))
                          .body("[0].company", is("Flexibus"))
                          .body("[1].company", is("Transdev"))
                          .body("[2].company", is("Flexibus"));

        verify(service, times(1)).getAllBuses();
    }

    @Test
    void whenGetBusById_thenReturnBus() {
        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        when(service.getBus(1L)).thenReturn(bus);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/bus/1")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("capacity", is(50))
                          .body("company", is("Flexibus"));

        verify(service, times(1)).getBus(1L);
    }

    @Test
    void whenGetBusByInvalidId_thenReturnNotFound() {
        when(service.getBus(1L)).thenReturn(null);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/bus/1")
                          .then().statusCode(HttpStatus.NOT_FOUND.value());

        verify(service, times(1)).getBus(1L);
    }

    @Test
    void whenUpdateBus_thenUpdateBus() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        when(service.updateBus(Mockito.any(Bus.class))).then(returnsFirstArg());

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(ContentType.JSON).body(bus)
                          .when().put("/api/backoffice/bus/1")
                          .then().statusCode(HttpStatus.OK.value())
                          .body("capacity", is(50))
                          .body("company", is("Flexibus"));

        verify(service, times(1)).updateBus(Mockito.any(Bus.class));
    }

    @Test
    void whenDeleteBusById_thenDeleteBus() {
        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().delete("/api/backoffice/bus/1")
                          .then().statusCode(HttpStatus.OK.value());

        verify(service, times(1)).deleteBus(1L);
    }
}
