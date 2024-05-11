package pt.ua.deti.tqs.backend.controllers;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.controllers.backoffice.TripBackofficeController;
import pt.ua.deti.tqs.backend.dtos.TripSeatsMapDto;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.services.ReservationService;
import pt.ua.deti.tqs.backend.services.TripService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@WebMvcTest({TripController.class, TripBackofficeController.class})
class TripControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private TripService service;

    @MockBean
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    void whenPostTrip_thenCreateTrip() {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setPrice(10.0);

        when(service.createTrip(Mockito.any())).thenReturn(trip);

        RestAssuredMockMvc.given().mockMvc(mvc).contentType(ContentType.JSON).body(trip)
                          .when().post("/api/backoffice/trip")
                          .then().statusCode(201)
                          .body("price", is((float) trip.getPrice()))
                          .body("bus.capacity", is(trip.getBus().getCapacity()))
                          .body("bus.company", is(trip.getBus().getCompany()))
                          .body("departure.name", is(trip.getDeparture().getName()))
                          .body("departureTime", is(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("arrival.name", is(trip.getArrival().getName()))
                          .body("arrivalTime", is(trip.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)));

        verify(service, times(1)).createTrip(Mockito.any());
    }

    @Test
    void givenManyTrips_whenGetTrips_thenReturnJsonArray() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        Trip trip1 = new Trip();
        trip1.setId(1L);
        trip1.setDeparture(city1);
        trip1.setArrival(city2);
        trip1.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip1.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip1.setBus(bus);
        trip1.setPrice(10.0);

        Trip trip2 = new Trip();
        trip2.setId(2L);
        trip2.setDeparture(city2);
        trip2.setArrival(city1);
        trip2.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip2.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip2.setBus(bus);
        trip2.setPrice(10.0);

        when(service.getTrips(Mockito.any(), Mockito.eq(null))).thenReturn(List.of(trip1, trip2));

        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().get("/api/public/trip")
                          .then().statusCode(200)
                          .body("[0].price", is((float) trip1.getPrice()))
                          .body("[0].bus.capacity", is(trip1.getBus().getCapacity()))
                          .body("[0].bus.company", is(trip1.getBus().getCompany()))
                          .body("[0].departure.name", is(trip1.getDeparture().getName()))
                          .body("[0].departureTime",
                                is(trip1.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("[0].arrival.name", is(trip1.getArrival().getName()))
                          .body("[0].arrivalTime", is(trip1.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("[1].price", is((float) trip2.getPrice()))
                          .body("[1].bus.capacity", is(trip2.getBus().getCapacity()))
                          .body("[1].bus.company", is(trip2.getBus().getCompany()))
                          .body("[1].departure.name", is(trip2.getDeparture().getName()))
                          .body("[1].departureTime",
                                is(trip2.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME))
                          )
                          .body("[1].arrival.name", is(trip2.getArrival().getName()))
                          .body("[1].arrivalTime", is(trip2.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void givenManyTrips_whenGetTripsWithFilters_thenReturnJsonArray() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        Trip trip1 = new Trip();
        trip1.setId(1L);
        trip1.setDeparture(city1);
        trip1.setArrival(city2);
        trip1.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip1.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip1.setBus(bus);
        trip1.setPrice(10.0);

        Trip trip2 = new Trip();
        trip2.setId(2L);
        trip2.setDeparture(city2);
        trip2.setArrival(city1);
        trip2.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip2.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip2.setBus(bus);
        trip2.setPrice(10.0);

        when(service.getTrips(Mockito.any(), Mockito.eq(null))).thenReturn(List.of(trip1, trip2));

        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when()
                          .get("/api/public/trip?departure=1&arrival=2&departureTime=2024-03-29T00:00:00&seats=2")
                          .then().statusCode(200)
                          .body("[0].price", is((float) trip1.getPrice()))
                          .body("[0].bus.capacity", is(trip1.getBus().getCapacity()))
                          .body("[0].bus.company", is(trip1.getBus().getCompany()))
                          .body("[0].departure.name", is(trip1.getDeparture().getName()))
                          .body("[0].departureTime",
                                is(trip1.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("[0].arrival.name", is(trip1.getArrival().getName()))
                          .body("[0].arrivalTime", is(trip1.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void whenGetTripById_thenReturnTrip() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(48);
        bus.setCompany("Flexibus");

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setFreeSeats(48);
        trip.setPrice(10.0);

        TripSeatsMapDto tripSeatsMapDto = new TripSeatsMapDto(trip);

        when(service.getTripWithSeatsMap(1L, null)).thenReturn(tripSeatsMapDto);

        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().get("/api/public/trip/1")
                          .then().statusCode(200)
                          .body("price", is((float) trip.getPrice()))
                          .body("bus.capacity", is(trip.getBus().getCapacity()))
                          .body("bus.company", is(trip.getBus().getCompany()))
                          .body("departure.name", is(trip.getDeparture().getName()))
                          .body("departureTime", is(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("arrival.name", is(trip.getArrival().getName()))
                          .body("arrivalTime", is(trip.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("seatsMap", hasSize(4));

        verify(service, times(1)).getTripWithSeatsMap(1L, null);
    }

    @Test
    void whenGetTripByIdAndCurrencyUsd_thenReturnTrip() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(48);
        bus.setCompany("Flexibus");

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setPrice(10.0);
        trip.setFreeSeats(48);

        TripSeatsMapDto tripSeatsMapDto = new TripSeatsMapDto(trip);

        when(service.getTripWithSeatsMap(1L, Currency.USD)).thenReturn(tripSeatsMapDto);

        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().get("/api/public/trip/1?currency=USD")
                          .then().statusCode(200)
                          .body("price", is((float) trip.getPrice()))
                          .body("bus.capacity", is(trip.getBus().getCapacity()))
                          .body("bus.company", is(trip.getBus().getCompany()))
                          .body("departure.name", is(trip.getDeparture().getName()))
                          .body("departureTime", is(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("arrival.name", is(trip.getArrival().getName()))
                          .body("arrivalTime", is(trip.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)));

        verify(service, times(1)).getTripWithSeatsMap(1L, Currency.USD);
    }

    @Test
    void whenGetTripByInvalidId_thenReturnNotFound() throws Exception {
        when(service.getTripWithSeatsMap(1L, null)).thenReturn(null);

        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().get("/api/public/trip/1")
                          .then().statusCode(404);

        verify(service, times(1)).getTripWithSeatsMap(1L, null);
    }

    @Test
    void whenGetReservationsByTripId_thenReturnReservations() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setPrice(10.0);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTrip(trip);
        reservation.setSeats(Arrays.asList("1A"));

        when(reservationService.getReservationsByTripId(1L, null)).thenReturn(List.of(reservation));

        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().get("/api/backoffice/trip/1/reservations")
                          .then().statusCode(200)
                          .body("[0].seats", hasSize(1))
                          .body("[0].trip.id", is(1));

        verify(reservationService, times(1)).getReservationsByTripId(1L, null);
    }

    @Test
    void whenUpdateTrip_thenUpdateTrip() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDeparture(city1);
        trip.setArrival(city2);
        trip.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip.setBus(bus);
        trip.setPrice(10.0);

        when(service.updateTrip(Mockito.any())).thenReturn(trip);

        RestAssuredMockMvc.given().mockMvc(mvc).contentType(ContentType.JSON).body(trip)
                          .when().put("/api/backoffice/trip/1")
                          .then().statusCode(200)
                          .body("price", is((float) trip.getPrice()))
                          .body("bus.capacity", is(trip.getBus().getCapacity()))
                          .body("bus.company", is(trip.getBus().getCompany()))
                          .body("departure.name", is(trip.getDeparture().getName()))
                          .body("departureTime", is(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE_TIME)))
                          .body("arrival.name", is(trip.getArrival().getName()))
                          .body("arrivalTime", is(trip.getArrivalTime().format(DateTimeFormatter.ISO_DATE_TIME)));

        verify(service, times(1)).updateTrip(Mockito.any());
    }

    @Test
    void whenFindAllTripsOnBackoffice_thenReturnAllTrips() throws Exception {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Porto");

        City city2 = new City();
        city2.setId(2L);
        city2.setName("Lisboa");

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setCapacity(50);
        bus.setCompany("Flexibus");

        Trip trip1 = new Trip();
        trip1.setId(1L);
        trip1.setDeparture(city1);
        trip1.setArrival(city2);
        trip1.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip1.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip1.setBus(bus);
        trip1.setPrice(10.0);

        Trip trip2 = new Trip();
        trip2.setId(2L);
        trip2.setDeparture(city2);
        trip2.setArrival(city1);
        trip2.setDepartureTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        trip2.setArrivalTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
        trip2.setBus(bus);
        trip2.setPrice(10.0);

        when(service.getAllTrips()).thenReturn(List.of(trip1, trip2));


        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().get("/api/backoffice/trip")
                          .then().statusCode(200)
                          .body("$", hasSize(2));
    }

    @Test
    void whenDeleteTrip_thenDeleteTrip() throws Exception {
        RestAssuredMockMvc.given().mockMvc(mvc)
                          .when().delete("/api/backoffice/trip/1")
                          .then().statusCode(200);

        verify(service, times(1)).deleteTrip(1L);
    }
}
