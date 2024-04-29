package pt.ua.deti.tqs.backend.controllers;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.services.ReservationService;
import pt.ua.deti.tqs.backend.services.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private ReservationService reservationService;

    @Test
    void whenPostUser_thenCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");

        when(service.createUser(Mockito.any())).thenReturn(user);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(user)
                          .when().post("/api/public/user")
                          .then().statusCode(201)
                          .body("id", is(1))
                          .body("name", is("John Doe"))
                          .body("email", is("johndoe@ua.pt"))
                          .body("username", is("johndoe"));

        verify(service, times(1)).createUser(Mockito.any());
    }

    @Test
    void whenGetUserById_thenGetUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");

        when(service.getUser(1L)).thenReturn(user);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/user/1")
                          .then().statusCode(200)
                          .body("id", is(1))
                          .body("name", is("John Doe"))
                          .body("email", is("johndoe@ua.pt"))
                          .body("username", is("johndoe"));

        verify(service, times(1)).getUser(1L);
    }

    @Test
    void whenGetUserByInvalidId_thenGetNull() {
        when(service.getUser(1L)).thenReturn(null);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/user/1")
                          .then().statusCode(404);

        verify(service, times(1)).getUser(1L);
    }

    @Test
    void whenGetUserByValidEmailAndPassword_thenGetUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");

        when(service.loginUser(user.getEmail(), user.getPassword())).thenReturn(user);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON)
                          .body("{\"email\":\"johndoe@ua.pt\",\"password\":\"password\"}")
                          .when().post("/api/public/user/login")
                          .then().statusCode(200)
                          .body("id", is(1))
                          .body("name", is(user.getName()))
                          .body("email", is(user.getEmail()))
                          .body("username", is(user.getUsername()));
    }

    @Test
    void whenGetUserByInvalidEmailAndPassword_thenGetNull() {
        User user = new User();
        user.setEmail("wrongEmail");
        user.setPassword("wrongPassword");

        when(service.loginUser("wrongEmail", "wrongPassword")).thenReturn(null);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(user)
                          .when().post("/api/public/user/login")
                          .then().statusCode(401);
    }

    @Test
    void whenGetUserReservationsByUserId_thenGetUserReservations() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johhdoe@ua.pt");
        user.setPassword("password");

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);

        when(reservationService.getReservationsByUserId(1L, null)).thenReturn(List.of(reservation));

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/user/1/reservations")
                          .then().statusCode(200)
                          .body("$", hasSize(1))
                          .body("[0].id", is(1));

        verify(reservationService, times(1)).getReservationsByUserId(1L, null);
    }

    @Test
    void whenUpdateUser_thenUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");

        when(service.updateUser(Mockito.any(User.class))).then(returnsFirstArg());

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(user)
                          .when().put("/api/public/user/1")
                          .then().statusCode(200)
                          .body("id", is(1))
                          .body("name", is("John Doe"))
                          .body("email", is("johndoe@ua.pt"))
                          .body("username", is("johndoe"));

        verify(service, times(1)).updateUser(Mockito.any(User.class));
    }

    @Test
    void whenDeleteUser_thenDeleteUser() {
        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().delete("/api/public/user/1")
                          .then().statusCode(200);

        verify(service, times(1)).deleteUser(1L);
    }
}
