package pt.ua.deti.tqs.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.services.ReservationService;
import pt.ua.deti.tqs.backend.services.UserService;

import java.util.List;

@RestController
@RequestMapping("public/user")
@Tag(name = "User")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a user")
    public ResponseEntity<User> getUser(@PathVariable @Parameter(name = "User ID", example = "1") Long id) {
        User user = userService.getUser(id);
        HttpStatus status = user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(user, status);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<User> getUser(@RequestBody User user) {
        User found = userService.loginUser(user.getEmail(), user.getPassword());
        HttpStatus status = found != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(found, status);
    }

    @GetMapping("{id}/reservations")
    @Operation(summary = "Get all reservations of a user")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(
            @PathVariable("id") @Parameter(name = "User ID", example = "1") Long id,
            @RequestParam(required = false) @Parameter(name = "Currency", example = "EUR") Currency currency) {
        return new ResponseEntity<>(reservationService.getReservationsByUserId(id, currency), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") @Parameter(name = "User ID", example = "1") Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        HttpStatus status = updated != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(updated, status);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") @Parameter(name = "User ID", example = "1") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
