package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.dtos.NormalUserDto;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User createNormalUser(NormalUserDto user) {
        return createUser(convertToNormalUser(user));
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getUserByEmail(email);
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
