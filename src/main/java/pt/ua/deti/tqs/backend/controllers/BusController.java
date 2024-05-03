package pt.ua.deti.tqs.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.services.BusService;

import java.util.List;
@RestController
@RequestMapping("api/public/bus")
@Tag(name = "Bus")
@AllArgsConstructor
public class BusController {

    private final BusService busService;

    @GetMapping
    @Operation(summary = "Get all buses")
    public ResponseEntity<List<Bus>> getBuses() {
        return new ResponseEntity<>(busService.getAllBuses(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a bus")
    public ResponseEntity<Bus> getBus(@PathVariable("id") @Parameter(name = "Bus ID", example = "1") Long id) {
        Bus bus = busService.getBus(id);
        HttpStatus status = bus != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(bus, status);
    }
}
