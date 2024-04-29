package pt.ua.deti.tqs.backend.controllers.backoffice;

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

import java.util.List;

@RestController
@RequestMapping("api/backoffice/reservation")
@Tag(name = "Reservation")
@AllArgsConstructor
public class ReservationBackofficeController {
    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Get all reservations")
    public ResponseEntity<List<Reservation>> getReservations(
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency
    ) {
        return new ResponseEntity<>(reservationService.getAllReservations(currency), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update a reservation")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable @Parameter(name = "Reservation ID", example = "1") Long id,
            @RequestBody Reservation reservation,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency) {
        reservation.setId(id);
        Reservation updated = reservationService.updateReservation(reservation, currency);
        HttpStatus status = updated != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(updated, status);
    }
}
