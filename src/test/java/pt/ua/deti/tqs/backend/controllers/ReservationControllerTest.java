package pt.ua.deti.tqs.backend.controllers;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.controllers.backoffice.ReservationBackofficeController;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.services.ReservationService;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@WebMvcTest(ReservationBackofficeController.class)
class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService service;

    @Test
    void whenPostReservation_thenCreateReservation() {
        User user = new User();
        user.setId(1L);

        Trip trip = new Trip();
        trip.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(2);
        reservation.setPrice(10.0);

        when(service.createReservation(Mockito.any(), eq(null))).thenReturn(reservation);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(reservation)
                          .when().post("/api/reservation")
                          .then().statusCode(201)
                          .body("seats", is(2))
                          .body("price", is(10.0F))
                          .body("user.id", is(1))
                          .body("trip.id", is(1));

        verify(service, times(1)).createReservation(Mockito.any(), eq(null));
    }

    @Test
    void givenManyReservations_whenGetReservations_thenReturnJsonArray() {
        User user = new User();
        user.setId(1L);

        Trip trip = new Trip();
        trip.setId(1L);

        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setTrip(trip);
        reservation1.setUser(user);
        reservation1.setSeats(2);
        reservation1.setPrice(10.0);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setTrip(trip);
        reservation2.setUser(user);
        reservation2.setSeats(3);
        reservation2.setPrice(15.0);

        Reservation reservation3 = new Reservation();
        reservation3.setId(3L);
        reservation3.setTrip(trip);
        reservation3.setUser(user);
        reservation3.setSeats(4);
        reservation3.setPrice(20.0);

        when(service.getAllReservations(null)).thenReturn(Arrays.asList(reservation1, reservation2, reservation3));

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/reservation")
                          .then().statusCode(200)
                          .body("$", hasSize(3))
                          .body("[0].seats", is(2))
                          .body("[0].price", is(10.0f))
                          .body("[0].user.id", is(1))
                          .body("[0].trip.id", is(1))
                          .body("[1].seats", is(3))
                          .body("[1].price", is(15.0f))
                          .body("[1].user.id", is(1))
                          .body("[1].trip.id", is(1))
                          .body("[2].seats", is(4))
                          .body("[2].price", is(20.0f))
                          .body("[2].user.id", is(1))
                          .body("[2].trip.id", is(1));

        verify(service, times(1)).getAllReservations(null);
    }

    @Test
    void whenGetReservationById_thenReturnReservation() {
        User user = new User();
        user.setId(1L);

        Trip trip = new Trip();
        trip.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(2);
        reservation.setPrice(10.0);

        when(service.getReservation(1L, null)).thenReturn(reservation);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/reservation/1")
                          .then().statusCode(200)
                          .body("seats", is(2))
                          .body("price", is(10.0f))
                          .body("user.id", is(1))
                          .body("trip.id", is(1));

        verify(service, times(1)).getReservation(1L, null);
    }

    @Test
    void whenGetReservationByInvalidId_thenReturnNotFound() {
        when(service.getReservation(1L, null)).thenReturn(null);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/reservation/1")
                          .then().statusCode(404);

        verify(service, times(1)).getReservation(1L, null);
    }

    @Test
    void whenUpdateReservation_thenUpdateReservation() {
        User user = new User();
        user.setId(1L);

        Trip trip = new Trip();
        trip.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setSeats(2);
        reservation.setPrice(10.0);

        when(service.updateReservation(Mockito.any(), Mockito.eq(null))).thenReturn(reservation);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(reservation)
                          .when().put("/api/reservation/1")
                          .then().statusCode(200)
                          .body("seats", is(2))
                          .body("price", is(10.0f))
                          .body("user.id", is(1))
                          .body("trip.id", is(1));

        verify(service, times(1)).updateReservation(Mockito.any(), Mockito.eq(null));
    }

    @Test
    void whenDeleteReservation_thenDeleteReservation() {
        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().delete("/api/reservation/1")
                          .then().statusCode(200);

        verify(service, times(1)).deleteReservationById(1L);
    }
}
