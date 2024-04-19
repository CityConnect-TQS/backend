package pt.ua.deti.tqs.backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.entities.Bus;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BusRepository busRepository;

    @Test
    void whenFindBusById_thenReturnBus() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        entityManager.persistAndFlush(bus);

        Bus found = busRepository.findById(bus.getId()).orElse(null);
        assertThat(found).isEqualTo(bus);
    }

    @Test
    void whenInvalidBusId_thenReturnNull() {
        Bus fromDb = busRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllBuses_thenReturnAllBuses() {
        Bus bus1 = new Bus();
        bus1.setCapacity(50);
        Bus bus2 = new Bus();
        bus2.setCapacity(100);
        Bus bus3 = new Bus();
        bus3.setCapacity(50);

        entityManager.persist(bus1);
        entityManager.persist(bus2);
        entityManager.persist(bus3);

        Iterable<Bus> allBuses = busRepository.findAll();
        assertThat(allBuses).hasSize(3).contains(bus1, bus2, bus3);
    }

    @Test
    void whenDeleteBusById_thenBusShouldNotExist() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        entityManager.persistAndFlush(bus);

        busRepository.deleteById(bus.getId());

        assertThat(busRepository.findById(bus.getId())).isEmpty();
    }

    @Test
    void whenUpdateBus_thenBusShouldBeUpdated() {
        Bus bus = new Bus();
        bus.setCapacity(50);
        entityManager.persistAndFlush(bus);

        bus.setCapacity(100);
        busRepository.save(bus);

        Bus updatedBus = busRepository.findById(bus.getId()).orElse(null);
        assertThat(updatedBus).isNotNull();
        assertThat(updatedBus.getCapacity()).isEqualTo(100);
    }
}
