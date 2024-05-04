package pt.ua.deti.tqs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequest {
    private String email;
    private String password;
}