package pt.ua.deti.tqs.backend.controllers;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.backend.components.JwtUtils;
import pt.ua.deti.tqs.backend.controllers.backoffice.StatsController;
import pt.ua.deti.tqs.backend.services.CustomUserDetailsService;
import pt.ua.deti.tqs.backend.services.StatsService;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@WebMvcTest(StatsController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService service;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService userService;

    @Test
    void whenGetStats_thenReturnStats() {
        when(service.getTotalRequests()).thenReturn(1);
        when(service.getCacheMisses()).thenReturn(0);

        RestAssuredMockMvc.given().mockMvc(mockMvc)
                          .when().get("/api/backoffice/stats")
                          .then().statusCode(200)
                          .body("totalRequests", is(1))
                          .body("cacheMisses", is(0));

        verify(service, times(1)).getTotalRequests();
        verify(service, times(1)).getCacheMisses();
    }
}