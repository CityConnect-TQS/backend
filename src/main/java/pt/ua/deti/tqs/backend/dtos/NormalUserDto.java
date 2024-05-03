package pt.ua.deti.tqs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NormalUserDto {
    private String username;
    private String name;
    private String email;
    private String password;
}
