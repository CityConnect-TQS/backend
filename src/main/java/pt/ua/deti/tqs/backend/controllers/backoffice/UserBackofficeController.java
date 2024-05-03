package pt.ua.deti.tqs.backend.controllers.backoffice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.services.UserService;

@RestController
@RequestMapping("api/backoffice/user")
@Tag(name = "User")
@AllArgsConstructor
public class UserBackofficeController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") @Parameter(name = "User ID", example = "1") Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        HttpStatus status = updated != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(updated, status);
    }
}
