package pt.ua.deti.tqs.backend.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class CurrencyApiResponseTest {
    @Test
    void testConstructor() {
        Map<Currency, Double> data = Map.of(Currency.USD, 1.08, Currency.EUR, 1.0, Currency.GBP, 0.92);
        CurrencyApiResponse actualCurrencyApiResponse = new CurrencyApiResponse(data);

        Assertions.assertEquals(data, actualCurrencyApiResponse.getData());
    }

    @Test
    void testNoArgsConstructor() {
        CurrencyApiResponse actualCurrencyApiResponse = new CurrencyApiResponse();

        Assertions.assertNull(actualCurrencyApiResponse.getData());
    }
}
