package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@ua.pt");
        user1.setUsername("john");
        user1.setPassword("john123");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@ua.pt");
        user2.setUsername("jane");
        user2.setPassword("jane123");
        User user3 = new User();
        user3.setId(3L);
        user3.setName("Alice");
        user3.setEmail("alice@ua.pt");
        user3.setUsername("alice");
        user3.setPassword("alice123");

        List<User> allUsers = List.of(user1, user2, user3);

        Mockito.when(userRepository.findById(12345L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findUserByEmailAndPassword(user1.getEmail(), user1.getPassword()))
               .thenReturn(user1);
        Mockito.when(userRepository.findUserByEmailAndPassword("wrongEmail", "wrongPassword")).thenReturn(null);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
    }

    @Test
    void whenSearchValidId_thenUserShouldBeFound() {
        User found = userService.getUser(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getEmail()).isEqualTo("john@ua.pt");
        assertThat(found.getUsername()).isEqualTo("john");
        assertThat(found.getPassword()).isEqualTo("john123");
    }

    @Test
    void whenSearchInvalidId_thenUserShouldNotBeFound() {
        User fromDb = userService.getUser(12345L);
        assertThat(fromDb).isNull();
    }

    @Test
    void whenSearchValidEmailAndPassword_thenUserShouldBeFound() {
        User user = new User();
        user.setEmail("john@ua.pt");
        user.setPassword("john123");

        User found = userService.loginUser(user.getEmail(), user.getPassword());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
        assertThat(found.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void whenSearchInvalidEmailAndPassword_thenUserShouldNotBeFound() {
        User user = new User();
        user.setEmail("wrongEmail");
        user.setPassword("wrongPassword");

        User found = userService.loginUser(user.getEmail(), user.getPassword());

        assertThat(found).isNull();
    }
}
