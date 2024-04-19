package pt.ua.deti.tqs.backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ua.deti.tqs.backend.entities.User;

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
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
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
        user1.setUsername("johndoe");
        user1.setName("John Doe");
        user1.setEmail("johndoe@ua.pt");
        user1.setPassword("password");
        User user2 = new User();
        user2.setUsername("janedoe");
        user2.setName("Jane Doe");
        user2.setEmail("janedoe@ua.pt");
        user2.setPassword("password");
        User user3 = new User();
        user3.setUsername("johndoe2");
        user3.setName("John Doe");
        user3.setEmail("johndoe2@ua.pt");
        user3.setPassword("password");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        Iterable<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(3).contains(user1, user2, user3);
    }

    @Test
    void whenDeleteUserById_thenUserShouldNotExist() {
        User user = new User();
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("joghndoe@ua.pt");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        userRepository.deleteById(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void whenUpdateUser_thenUserShouldBeUpdated() {
        User user = new User();
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("johndoe@ua.pt");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        user.setName("Jane Doe");
        userRepository.save(user);

        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Jane Doe");
    }
}
