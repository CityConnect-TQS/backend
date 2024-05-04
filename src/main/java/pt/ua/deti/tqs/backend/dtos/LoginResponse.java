package pt.ua.deti.tqs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pt.ua.deti.tqs.backend.constants.UserRole;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final Long id;
    private final String name;
    private final String email;
    private List<UserRole> roles;
    private final String token;
    private final Long expires;
}
