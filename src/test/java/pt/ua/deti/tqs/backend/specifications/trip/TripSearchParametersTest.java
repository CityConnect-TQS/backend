package pt.ua.deti.tqs.backend.specifications.trip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TripSearchParametersTest {
    @Test
    void testConstructor() {
        LocalDateTime now = LocalDateTime.now();
        TripSearchParameters params = new TripSearchParameters(1L, 2L, now, 1L);

        Assertions.assertEquals(1, params.getDeparture());
        Assertions.assertEquals(2, params.getArrival());
        Assertions.assertEquals(now, params.getDepartureTime());
        Assertions.assertEquals(1, params.getFreeSeats());

    }
}
