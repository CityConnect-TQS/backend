package pt.ua.deti.tqs.backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.entities.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindUserById_thenReturnUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        user.setRoles(List.of(UserRole.USER));
        entityManager.persistAndFlush(user);

        User found = userRepository.findById(user.getId()).orElse(null);
        assertThat(found).isEqualTo(user);
    }

    @Test
    void whenInvalidUserId_thenReturnNull() {
        User fromDb = userRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenFindAllUsers_thenReturnAllUsers() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("johndoe@ua.pt");
        user1.setPassword("password");
        user1.setRoles(List.of(UserRole.USER));
        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("janedoe@ua.pt");
        user2.setPassword("password");
        user2.setRoles(List.of(UserRole.USER, UserRole.STAFF));
        User user3 = new User();
        user3.setName("John Doe");
        user3.setEmail("johndoe2@ua.pt");
        user3.setPassword("password");
        user3.setRoles(List.of(UserRole.USER, UserRole.STAFF, UserRole.ADMIN));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        Iterable<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(3).contains(user1, user2, user3);
    }

    @Test
    void whenDeleteUserById_thenUserShouldNotExist() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("joghndoe@ua.pt");
        user.setPassword("password");
        user.setRoles(List.of(UserRole.USER));
        entityManager.persistAndFlush(user);

        userRepository.deleteById(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}
