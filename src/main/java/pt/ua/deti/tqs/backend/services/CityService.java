package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.repositories.CityRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public City createCity(City city) {
        return cityRepository.save(city);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<City> getCitiesByName(String name) {
        return cityRepository.findByNameContainingIgnoreCase(name);
    }

    public City getCity(Long id) {
        return cityRepository.findById(id).orElse(null);
    }

    public City updateCity(City city) {
        Optional<City> existingOpt = cityRepository.findById(city.getId());

        if (existingOpt.isEmpty()) {
            return null;
        }

        City existing = existingOpt.get();
        existing.setName(city.getName());
        existing.setLatitude(city.getLatitude());
        existing.setLongitude(city.getLongitude());
        return cityRepository.save(existing);
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }
}
