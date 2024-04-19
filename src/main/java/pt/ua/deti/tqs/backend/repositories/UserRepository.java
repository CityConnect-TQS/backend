package pt.ua.deti.tqs.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ua.deti.tqs.backend.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmailAndPassword(String email, String password);
}
