package pt.ua.deti.tqs.backend.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("ROLE_USER"),
    STAFF("ROLE_STAFF"),
    ADMIN("ROLE_ADMIN");

    private final String roleName;
}
