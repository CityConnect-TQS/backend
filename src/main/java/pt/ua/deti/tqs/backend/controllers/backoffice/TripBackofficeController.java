package pt.ua.deti.tqs.backend.controllers.backoffice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.services.ReservationService;
import pt.ua.deti.tqs.backend.services.TripService;

import java.util.List;

@RestController
@RequestMapping("api/backoffice/trip")
@Tag(name = "Trip")
@AllArgsConstructor
public class TripBackofficeController {
    private final TripService tripService;
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new trip")
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
        return new ResponseEntity<>(tripService.createTrip(trip), HttpStatus.CREATED);
    }

    @GetMapping("{id}/reservations")
    @Operation(summary = "Get all reservations of a trip")
    public ResponseEntity<List<Reservation>> getReservationsByTripId(
            @PathVariable @Parameter(name = "Trip ID", example = "1") Long id,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency) {
        List<Reservation> reservations = reservationService.getReservationsByTripId(id, currency);
        HttpStatus status = !reservations.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(reservations, status);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update a trip")
    public ResponseEntity<Trip> updateTrip(
            @PathVariable @Parameter(name = "Trip ID", example = "1") Long id,
            @RequestBody Trip trip) {
        trip.setId(id);
        Trip updated = tripService.updateTrip(trip);
        HttpStatus status = updated != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(updated, status);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a trip")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable @Parameter(name = "Trip ID", example = "1") Long id) {
        tripService.deleteTrip(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
