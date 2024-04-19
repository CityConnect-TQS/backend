package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.backend.helpers.Currency;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock(lenient = true)
    private ApiService apiService;

    @Mock(lenient = true)
    private StatsService statsService;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    public void setUp() {
        Mockito.when(apiService.fetchRates()).thenReturn(Map.of(Currency.USD, 1.08, Currency.EUR, 1.0));
    }

    @Test
    void whenConvertEurToCurrency_thenReturnConvertedValue() {
        double convertedValue = currencyService.convertEurToCurrency(1.0, Currency.USD);

        assertThat(convertedValue).isEqualTo(1.08);
    }

    @Test
    void whenConvertCurrencyToEur_thenReturnConvertedValue() {
        double convertedValue = currencyService.convertCurrencyToEur(1.08, Currency.USD);

        assertThat(convertedValue).isEqualTo(1.0);
    }

    @Test
    void whenConvertCurrencyToEur_givenEur_thenReturnSameValue() {
        double convertedValue = currencyService.convertCurrencyToEur(1.0, Currency.EUR);

        assertThat(convertedValue).isEqualTo(1.0);
    }

    @Test
    void whenConvertEurToCurrency_givenEur_thenReturnSameValue() {
        double convertedValue = currencyService.convertEurToCurrency(1.0, Currency.EUR);

        assertThat(convertedValue).isEqualTo(1.0);
    }

    @Test
    void whenConvertCurrencyToEur_givenZeroAmount_thenReturnZero() {
        double convertedValue = currencyService.convertCurrencyToEur(0.0, Currency.USD);

        assertThat(convertedValue).isEqualTo(0.0);
    }

    @Test
    void whenConvertEurToCurrency_givenZeroAmount_thenReturnZero() {
        double convertedValue = currencyService.convertEurToCurrency(0.0, Currency.USD);

        assertThat(convertedValue).isEqualTo(0.0);
    }
}
