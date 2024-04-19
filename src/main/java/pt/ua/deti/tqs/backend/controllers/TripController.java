package pt.ua.deti.tqs.backend.controllers;

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
import pt.ua.deti.tqs.backend.specifications.trip.TripSearchParameters;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/trip")
@Tag(name = "Trip")
@AllArgsConstructor
public class TripController {
    private final TripService tripService;
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new trip")
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
        return new ResponseEntity<>(tripService.createTrip(trip), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get trips")
    public ResponseEntity<List<Trip>> getTrips(
            @RequestParam(required = false) @Parameter(name = "Departure", example = "1") Long departure,
            @RequestParam(required = false) @Parameter(name = "Arrival", example = "1") Long arrival,
            @RequestParam(required = false) @Parameter(name = "Departure time", example = "1970-01-01T00:00:00") LocalDateTime departureTime,
            @RequestParam(required = false) @Parameter(name = "Minimum number of seats", example = "1") Long seats,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency
    ) {
        TripSearchParameters params = new TripSearchParameters(departure, arrival, departureTime, seats);
        return new ResponseEntity<>(tripService.getTrips(params, currency),
                                    HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a trip")
    public ResponseEntity<Trip> getTrip(
            @PathVariable @Parameter(name = "Trip ID", example = "1") Long id,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency) {
        Trip trip = tripService.getTrip(id, currency);
        HttpStatus status = trip != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(trip, status);
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
