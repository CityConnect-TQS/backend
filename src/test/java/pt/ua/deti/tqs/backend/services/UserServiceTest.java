package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void whenCreateNormalUser_thenUserShouldBeCreated() {
        NormalUserDto user = new NormalUserDto("John", "john@ua.pt", "john123");
        LoginResponse created = userService.createNormalUser(user);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(user.getName());
        assertThat(created.getEmail()).isEqualTo(user.getEmail());
        // assertThat(created.getPassword()).isEqualTo(user.getPassword());
        assertThat(created.getRoles()).isEqualTo(List.of(UserRole.USER));
    }

    @Test
    @Disabled
    void whenCreateUser_thenUserShouldBeCreated() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@ua.pt");
        user.setPassword("john123");
        user.setRoles(List.of(UserRole.USER, UserRole.STAFF));

        LoginResponse created = userService.createUser(user);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(user.getName());
        assertThat(created.getEmail()).isEqualTo(user.getEmail());
        assertThat(created.getRoles()).isEqualTo(user.getRoles());
    }

    @Test
    void whenUpdateNormalUser_thenUserShouldBeUpdated() {
        NormalUserDto user = new NormalUserDto("John", "john@ua.pt", "john123");
        User updated = userService.updateNormalUser(1L, user);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(user.getName());
        assertThat(updated.getEmail()).isEqualTo(user.getEmail());
        assertThat(updated.getPassword()).isEqualTo(user.getPassword());
        assertThat(updated.getRoles()).isEqualTo(List.of(UserRole.USER));
    }

    @Test
    void whenUpdateUser_thenUserShouldBeUpdated() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@ua.pt");
        user.setPassword("john123");
        user.setRoles(List.of(UserRole.USER, UserRole.STAFF));

        User updated = userService.updateUser(1L, user);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(user.getName());
        assertThat(updated.getEmail()).isEqualTo(user.getEmail());
        assertThat(updated.getPassword()).isEqualTo(user.getPassword());
        assertThat(updated.getRoles()).isEqualTo(user.getRoles());
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
    @Disabled("Waiting for new login implementation")
    void whenSearchValidEmailAndPassword_thenUserShouldBeFound() {
        User user = new User();
        user.setEmail("john@ua.pt");
        user.setPassword("john123");

        // User found = userService.loginUser(user.getEmail(), user.getPassword());
        User found = user;

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
        assertThat(found.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Disabled("Waiting for new login implementation")
    void whenSearchInvalidEmailAndPassword_thenUserShouldNotBeFound() {
        User user = new User();
        user.setEmail("wrongEmail");
        user.setPassword("wrongPassword");

        // User found = userService.loginUser(user.getEmail(), user.getPassword());
        User found = null;

        assertThat(found).isNull();
    }
}
