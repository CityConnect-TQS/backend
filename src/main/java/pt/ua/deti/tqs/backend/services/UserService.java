package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User loginUser(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password);
    }

    public User updateUser(User user) {
        Optional<User> existingOpt = userRepository.findById(user.getId());

        if (existingOpt.isEmpty()) {
            return null;
        }

        User existing = existingOpt.get();
        existing.setUsername(user.getUsername());
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
