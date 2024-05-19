package pt.ua.deti.tqs.backend.integrations;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import pt.ua.deti.tqs.backend.components.JwtUtils;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.helpers.CurrencyApiResponse;
import pt.ua.deti.tqs.backend.services.ApiService;
import pt.ua.deti.tqs.backend.services.CustomUserDetailsService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"trip.status.update.delay=1000"})
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class ApiServiceTestIT {
    @MockBean
    private RestTemplate restTemplate;

    @Mock(lenient = true)
    private CurrencyApiResponse currencyApiResponse;

    @Autowired
    @InjectMocks
    private ApiService apiService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService userService;

    @BeforeEach
    public void setUp() {
        Mockito.when(currencyApiResponse.getData())
               .thenReturn(Map.of(Currency.USD, 1.08, Currency.EUR, 1.0, Currency.GBP, 0.92));

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(CurrencyApiResponse.class)))
               .thenReturn(new ResponseEntity<>(currencyApiResponse, null, 200));
    }

    @AfterEach
    public void tearDown() {
        apiService.emptyRatesCache();
    }

    @Test
    void whenFetchRates_thenRatesAreFetched() {
        Map<Currency, Double> fetched = apiService.fetchRates();
        assertThat(fetched).isNotNull().contains(
                Map.entry(Currency.USD, 1.08),
                Map.entry(Currency.EUR, 1.0),
                Map.entry(Currency.GBP, 0.92)
        );

        Mockito.verify(restTemplate, Mockito.times(1))
               .getForEntity(Mockito.anyString(), Mockito.eq(CurrencyApiResponse.class));
    }

    @Test
    void whenFetchRatesWithHypotheticalFailure_thenReturnNull() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(CurrencyApiResponse.class)))
               .thenReturn(new ResponseEntity<>(null, null, 200));

        Map<Currency, Double> fetched = apiService.fetchRates();
        assertThat(fetched).isNull();

        Mockito.verify(restTemplate, Mockito.times(1))
               .getForEntity(Mockito.anyString(), Mockito.eq(CurrencyApiResponse.class));
    }

    @Test
    void whenFetchRatesTwice_thenCacheIsUsed() {
        apiService.fetchRates();
        apiService.fetchRates();
        Mockito.verify(restTemplate, Mockito.times(1))
               .getForEntity(Mockito.anyString(), Mockito.eq(CurrencyApiResponse.class));
    }
}
