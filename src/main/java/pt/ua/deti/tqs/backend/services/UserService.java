package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.dtos.NormalUserDto;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User createNormalUser(NormalUserDto user) {
        return createUser(convertToNormalUser(user));
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User loginUser(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password);
    }

    public User updateUser(Long id, User user) {
        Optional<User> existingOpt = userRepository.findById(id);

        if (existingOpt.isEmpty()) {
            return null;
        }

        User existing = existingOpt.get();
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
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
