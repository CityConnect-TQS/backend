package pt.ua.deti.tqs.backend.controllers;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.components.JwtUtils;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.controllers.backoffice.UserBackofficeController;
import pt.ua.deti.tqs.backend.dtos.LoginRequest;
import pt.ua.deti.tqs.backend.dtos.LoginResponse;
import pt.ua.deti.tqs.backend.dtos.NormalUserDto;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.services.CustomUserDetailsService;
import pt.ua.deti.tqs.backend.services.ReservationService;
import pt.ua.deti.tqs.backend.services.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserBackofficeController.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService userService;

    @Test
    void whenPostNormalUser_thenCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        user.setRoles(List.of(UserRole.USER));

        NormalUserDto normalUserDto = new NormalUserDto("John Doe", "johndoe@ua.pt", "password");

        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getName(), user.getEmail(),
                                                        user.getRoles(), "token", 123456789L);

        when(service.createNormalUser(Mockito.any(NormalUserDto.class))).thenReturn(loginResponse);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(normalUserDto)
                          .when().post("/api/public/user")
                          .then().statusCode(201)
                          .body("id", is(1))
                          .body("name", is(user.getName()))
                          .body("email", is(user.getEmail()))
                          .body("roles", hasSize(1))
                          .body("roles[0]", is(UserRole.USER.toString()));

        verify(service, times(1)).createNormalUser(Mockito.any(NormalUserDto.class));
    }

    @Test
    void whenPostUser_thenCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        user.setRoles(List.of(UserRole.USER, UserRole.STAFF));

        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getName(), user.getEmail(),
                                                        user.getRoles(), "token", 123456789L);

        when(service.createUser(Mockito.any(User.class))).thenReturn(loginResponse);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(user)
                          .when().post("/api/backoffice/user")
                          .then().statusCode(201)
                          .body("id", is(1))
                          .body("name", is("John Doe"))
                          .body("email", is("johndoe@ua.pt"))
                          .body("roles", hasSize(2))
                          .body("roles[0]", is(UserRole.USER.toString()))
                          .body("roles[1]", is(UserRole.STAFF.toString()));

        verify(service, times(1)).createUser(Mockito.any(User.class));
    }

    @Test
    void whenGetUserById_thenGetUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");

        when(service.getUser(1L)).thenReturn(user);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/public/user/1")
                          .then().statusCode(200)
                          .body("id", is(1))
                          .body("name", is("John Doe"))
                          .body("email", is("johndoe@ua.pt"));

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
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");

        LoginRequest loginRequest = new LoginRequest("johndoe@ua.pt", "password");

        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getName(), user.getEmail(),
                                                        user.getRoles(), "token", 123456789L);

        when(service.loginUser(Mockito.any(LoginRequest.class))).thenReturn(loginResponse);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(loginRequest)
                          .when().post("/api/public/user/login")
                          .then().statusCode(200)
                          .body("id", is(1))
                          .body("name", is("John Doe"))
                          .body("email", is("johndoe@ua.pt"));

        verify(service, times(1)).loginUser(Mockito.any(LoginRequest.class));
    }

    @Test
    void whenGetUserByInvalidEmailAndPassword_thenGetNull() {
        User user = new User();
        user.setEmail("wrongEmail");
        user.setPassword("wrongPassword");

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(user)
                          .when().post("/api/public/user/login")
                          .then().statusCode(401);
    }

    @Test
    void whenGetUserReservationsByUserId_thenGetUserReservations() {
        User user = new User();
        user.setId(1L);
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
    void whenUpdateNormalUser_thenUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        user.setRoles(List.of(UserRole.USER));

        NormalUserDto normalUserDto = new NormalUserDto("John Doe", "johndoe@ua.pt", "password");
        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getName(), user.getEmail(),
                                                        user.getRoles(), "token", 123456789L);

        when(service.updateNormalUser(Mockito.any(Long.class), Mockito.any(NormalUserDto.class))).thenReturn(
                loginResponse);

        RestAssuredMockMvc.given().mockMvc(mockMvc).contentType(MediaType.APPLICATION_JSON).body(normalUserDto)
                          .when().put("/api/public/user/1")
                          .then().statusCode(200)
                          .body("id", is(1))
                          .body("name", is(user.getName()))
                          .body("email", is(user.getEmail()))
                          .body("roles", hasSize(1))
                          .body("roles[0]", is(UserRole.USER.toString()));

        verify(service, times(1)).updateNormalUser(Mockito.any(Long.class), Mockito.any(NormalUserDto.class));
    }

    @Test
    void whenUpdateUser_thenUpdateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        user.setRoles(List.of(UserRole.USER, UserRole.STAFF));

        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles(), "token", 123456789L);

        when(service.updateUser(Mockito.any(Long.class), Mockito.any(User.class))).thenReturn(loginResponse);

        mockMvc.perform(put("/api/backoffice/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("johndoe@ua.pt")))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[0]", is(UserRole.USER.toString())))
                .andExpect(jsonPath("$.roles[1]", is(UserRole.STAFF.toString())));

        verify(service, times(1)).updateUser(Mockito.any(Long.class), Mockito.any(User.class));
    }

    @Test
    void whenDeleteUser_thenDeleteUser() {
        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().delete("/api/public/user/1")
                          .then().statusCode(200);

        verify(service, times(1)).deleteUser(1L);
    }
}
