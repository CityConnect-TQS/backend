package pt.ua.deti.tqs.backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.entities.City;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CityRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void whenFindCityById_thenReturnCity() {
        City city = new City();
        city.setName("Aveiro");
        entityManager.persistAndFlush(city);

        City found = cityRepository.findById(city.getId()).orElse(null);
        assertThat(found).isEqualTo(city);
    }

    @Test
    void whenFindCityByName_thenReturnCity() {
        City city = new City();
        city.setName("Aveiro");
        entityManager.persistAndFlush(city);

        List<City> found = cityRepository.findByNameContainingIgnoreCase(city.getName());
        assertThat(found).hasSize(1).contains(city);
    }

    @Test
    void whenInvalidCityId_thenReturnNull() {
        City fromDb = cityRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllCities_thenReturnAllCities() {
        City city1 = new City();
        city1.setName("Aveiro");
        City city2 = new City();
        city2.setName("Porto");
        City city3 = new City();
        city3.setName("Lisboa");

        entityManager.persist(city1);
        entityManager.persist(city2);
        entityManager.persist(city3);

        Iterable<City> allCities = cityRepository.findAll();
        assertThat(allCities).hasSize(3).contains(city1, city2, city3);
    }

    @Test
    void whenDeleteCityById_thenCityShouldNotExist() {
        City city = new City();
        city.setName("Aveiro");
        entityManager.persistAndFlush(city);

        cityRepository.deleteById(city.getId());
        assertThat(cityRepository.findById(city.getId())).isEmpty();
    }
}
