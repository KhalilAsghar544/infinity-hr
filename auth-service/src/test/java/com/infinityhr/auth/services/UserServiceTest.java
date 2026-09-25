package com.infinityhr.auth.services;

import com.infinityhr.auth.config.SecurityConfig;
import com.infinityhr.auth.entities.User;
import com.infinityhr.auth.enums.UserStatus;
import com.infinityhr.auth.exceptions.DuplicateResourceException;
import com.infinityhr.auth.exceptions.ResourceNotFoundException;
import com.infinityhr.auth.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserService.class, SecurityConfig.class})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordIsStoredAsBcryptHashNotPlainText() {
        User user = userService.createUser("sara", "sara@infinityhr.com", "S3cure!Pass", Set.of("EMPLOYEE"));

        assertThat(user.getPasswordHash())
                .startsWith("{bcrypt}")
                .doesNotContain("S3cure!Pass");
        assertThat(passwordEncoder.matches("S3cure!Pass", user.getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches("wrong-password", user.getPasswordHash())).isFalse();
    }

    @Test
    void samePasswordGivesDifferentHashesBecauseOfTheSalt() {
        User first = userService.createUser("user.one", "one@infinityhr.com", "Same!Pass1", Set.of("EMPLOYEE"));
        User second = userService.createUser("user.two", "two@infinityhr.com", "Same!Pass1", Set.of("EMPLOYEE"));

        assertThat(first.getPasswordHash()).isNotEqualTo(second.getPasswordHash());
    }

    @Test
    void rejectsDuplicateUsernameAndEmail() {
        userService.createUser("hamza", "hamza@infinityhr.com", "Pass!word1", Set.of("EMPLOYEE"));

        assertThatThrownBy(() -> userService.createUser("HAMZA", "new@infinityhr.com", "Pass!word1", Set.of("EMPLOYEE")))
                .isInstanceOf(DuplicateResourceException.class);
        assertThatThrownBy(() -> userService.createUser("hamza2", "Hamza@InfinityHR.com", "Pass!word1", Set.of("EMPLOYEE")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void rejectsUnknownRole() {
        assertThatThrownBy(() -> userService.createUser("zara", "zara@infinityhr.com", "Pass!word1", Set.of("CEO")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void newUserIsPendingUntilActivated() {
        User user = userService.createUser("omar", "omar@infinityhr.com", "Pass!word1", Set.of("EMPLOYEE"));
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_ACTIVATION);

        userService.activateUser(user.getId());

        assertThat(userRepository.findById(user.getId()).orElseThrow().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
