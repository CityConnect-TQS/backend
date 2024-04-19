package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.repositories.BusRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {
    @Mock(lenient = true)
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

    @BeforeEach
    public void setUp() {
        Bus bus1 = new Bus();
        bus1.setId(1L);
        bus1.setCapacity(50);
        Bus bus2 = new Bus();
        bus2.setId(2L);
        bus2.setCapacity(100);
        Bus bus3 = new Bus();
        bus3.setId(3L);
        bus3.setCapacity(50);

        List<Bus> allBuses = List.of(bus1, bus2, bus3);

        Mockito.when(busRepository.findById(12345L)).thenReturn(Optional.empty());
        Mockito.when(busRepository.findById(bus1.getId())).thenReturn(Optional.of(bus1));
        Mockito.when(busRepository.findAll()).thenReturn(allBuses);
    }

    @Test
    void whenSearchValidId_thenBusShouldBeFound() {
        Bus found = busService.getBus(1L);

        assertThat(found).isNotNull();
        assertThat(found.getCapacity()).isEqualTo(50);
    }

    @Test
    void whenSearchInvalidId_thenBusShouldNotBeFound() {
        Bus fromDb = busService.getBus(12345L);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllBuses_thenReturnAllBuses() {
        List<Bus> allBuses = busService.getAllBuses();
        assertThat(allBuses).hasSize(3);
    }
}
