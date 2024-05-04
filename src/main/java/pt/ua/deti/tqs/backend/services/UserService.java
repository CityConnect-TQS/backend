package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.components.JwtUtils;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.dtos.LoginRequest;
import pt.ua.deti.tqs.backend.dtos.LoginResponse;
import pt.ua.deti.tqs.backend.dtos.NormalUserDto;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public LoginResponse createUser(User user) {
        if (userRepository.findUserByEmail(user.getEmail()) != null) {
            return null;
        }

        String unencryptedPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return this.loginUser(new LoginRequest(user.getEmail(), unencryptedPassword));
    }

    public LoginResponse createNormalUser(NormalUserDto user) {
        return createUser(convertToNormalUser(user));
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        User user = this.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return null;
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        Long expires = jwtUtils.getExpirationFromJwtToken(jwt).getTime();

        User userDetails = (User) authentication.getPrincipal();

        return new LoginResponse(userDetails.getId(), userDetails.getName(), userDetails.getEmail(),
                                 userDetails.getRoles(), jwt, expires);
    }

    public User updateUser(Long id, User user) {
        Optional<User> existingOpt = userRepository.findById(id);

        if (existingOpt.isEmpty()) {
            return null;
        }

        User existing = existingOpt.get();
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(passwordEncoder.encode(user.getPassword()));
        existing.setRoles(user.getRoles());
        return userRepository.save(existing);
    }

    public User updateNormalUser(Long id, NormalUserDto user) {
        return updateUser(id, convertToNormalUser(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private User convertToNormalUser(NormalUserDto userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRoles(List.of(UserRole.USER));
        return user;
    }
}
