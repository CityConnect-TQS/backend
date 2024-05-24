package pt.ua.deti.tqs.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.services.ReservationService;

@RestController
@RequestMapping("api/public/reservation")
@Tag(name = "Reservation")
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<Reservation> createReservation(
            @RequestBody Reservation reservation,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency) {
        Reservation created = reservationService.createReservation(reservation, currency);
        HttpStatus status = created != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(created, status);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a reservation")
    public ResponseEntity<Reservation> getReservation(
            @PathVariable @Parameter(name = "Reservation ID", example = "1") Long id,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency) {
        Reservation reservation = reservationService.getReservation(id, currency);
        HttpStatus status = reservation != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(reservation, status);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a reservation")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable @Parameter(name = "Reservation ID", example = "1") Long id) {
        HttpStatus status = reservationService.deleteReservationById(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(status);
    }

    @PatchMapping("{id}/check-in")
    @Operation(summary = "Check-in a reservation")
    public ResponseEntity<Reservation> checkInReservation(
            @PathVariable @Parameter(name = "Reservation ID", example = "1") Long id) {
        Reservation checkedIn = reservationService.updateCheckedIn(id);
        HttpStatus status = checkedIn != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(checkedIn, status);
    }
}
