package pt.ua.deti.tqs.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.services.CityService;

import java.util.List;

@RestController
@RequestMapping("api/public/city")
@Tag(name = "City")
@AllArgsConstructor
public class CityController {

    private final CityService cityService;
    @GetMapping
    @Operation(summary = "Get cities")
    public ResponseEntity<List<City>> getCities(
            @RequestParam(required = false)
            @Parameter(name = "City name", examples = {@ExampleObject(name = "Aveiro"), @ExampleObject(name = "Ave")})
            String name) {
        if (name != null) {
            return new ResponseEntity<>(cityService.getCitiesByName(name), HttpStatus.OK);
        }
        return new ResponseEntity<>(cityService.getAllCities(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a city")
    public ResponseEntity<City> getCity(@PathVariable @Parameter(name = "City ID", example = "1") Long id) {
        City city = cityService.getCity(id);
        HttpStatus status = city != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(city, status);
    }
}
