package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @InjectMocks
    private StatsService service;

    @Test
    void whenIncreaseTotalRequests_thenTotalRequestsIsIncreased() {
        service.incrementTotalRequests();
        assertThat(service.getTotalRequests()).isEqualTo(1);
    }

    @Test
    void whenIncreaseCacheMisses_thenCacheMissesIsIncreased() {
        service.incrementCacheMisses();
        assertThat(service.getCacheMisses()).isEqualTo(1);
    }
}
