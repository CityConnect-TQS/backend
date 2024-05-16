package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.com.trilead.ssh2.auth.AuthenticationManager;
import pt.ua.deti.tqs.backend.components.JwtUtils;
import pt.ua.deti.tqs.backend.constants.UserRole;
import pt.ua.deti.tqs.backend.dtos.LoginResponse;
import pt.ua.deti.tqs.backend.dtos.NormalUserDto;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;


    @BeforeEach
    public void setUp() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@ua.pt");
        user1.setPassword("john123");
        user1.setRoles(List.of(UserRole.USER));

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@ua.pt");
        user2.setPassword("jane123");
        user2.setRoles(List.of(UserRole.USER, UserRole.ADMIN));

        User user3 = new User();
        user3.setId(3L);
        user3.setName("Alice");
        user3.setEmail("alice@ua.pt");
        user3.setPassword("alice123");
        user3.setRoles(List.of(UserRole.USER));

        List<User> allUsers = List.of(user1, user2, user3);

        Mockito.when(userRepository.save(Mockito.any(User.class))).then(returnsFirstArg());

        Mockito.when(userRepository.findById(12345L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(user1);
        Mockito.when(userRepository.findUserByEmail("wrongEmail")).thenReturn(null);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
    }

    @Test
    void whenSearchValidId_thenUserShouldBeFound() {
        User found = userService.getUser(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getEmail()).isEqualTo("john@ua.pt");
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
        user.setId(1L);
        user.setEmail("john@ua.pt");
        user.setPassword("john123");

        User found = userService.getUser(user.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getEmail()).isEqualTo("john@ua.pt");
    }

    @Test
    void whenSearchInvalidEmailAndPassword_thenUserShouldNotBeFound() {
        User user = new User();
        user.setEmail("wrongEmail");
        user.setPassword("wrongPassword");

        User fromDb = userService.getUser(user.getId());
        assertThat(fromDb).isNull();
    }
}
