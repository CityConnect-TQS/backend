package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.repositories.CityRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock(lenient = true)
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @BeforeEach
    public void setUp() {
        City city1 = new City();
        city1.setId(1L);
        city1.setName("Aveiro");
        City city2 = new City();
        city2.setId(2L);
        city2.setName("Porto");
        City city3 = new City();
        city3.setId(3L);
        city3.setName("Lisboa");

        List<City> allCities = List.of(city1, city2, city3);

        Mockito.when(cityRepository.findById(12345L)).thenReturn(Optional.empty());
        Mockito.when(cityRepository.findById(city1.getId())).thenReturn(Optional.of(city1));
        Mockito.when(cityRepository.findAll()).thenReturn(allCities);
        Mockito.when(cityRepository.findByNameContainingIgnoreCase("Aveiro")).thenReturn(List.of(city1));
        Mockito.when(cityRepository.findByNameContainingIgnoreCase("vei")).thenReturn(List.of(city1));
        Mockito.when(cityRepository.findByNameContainingIgnoreCase("Invalid")).thenReturn(List.of());
    }

    @Test
    void whenSearchValidId_thenCityShouldBeFound() {
        City found = cityService.getCity(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Aveiro");
    }

    @Test
    void whenSearchInvalidId_thenCityShouldNotBeFound() {
        City fromDb = cityService.getCity(12345L);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllCities_thenReturnAllCities() {
        List<City> allCities = cityService.getAllCities();

        assertThat(allCities).isNotNull();
        assertThat(allCities).hasSize(3).extracting(City::getName).contains("Aveiro", "Porto", "Lisboa");
    }

    @Test
    void whenFindCityByName_thenReturnCity() {
        List<City> found = cityService.getCitiesByName("Aveiro");

        assertThat(found).hasSize(1).extracting(City::getName).contains("Aveiro");
    }

    @Test
    void whenFindCityByInvalidName_thenReturnEmptyList() {
        List<City> found = cityService.getCitiesByName("Invalid");

        assertThat(found).isEmpty();
    }

    @Test
    void whenFindCityByPartialName_thenReturnCity() {
        List<City> found = cityService.getCitiesByName("vei");

        assertThat(found).hasSize(1).extracting(City::getName).contains("Aveiro");
    }
}
