package scope.dev.bank.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import scope.dev.bank.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class UserServiceTest {

    @Autowired UserService userService;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void storesOnlyAHashOfThePassword() {
        User user = userService.register("a-" + UUID.randomUUID() + "@example.com", "correct-horse-battery", "Alice");

        assertThat(user.getPasswordHash()).isNotEqualTo("correct-horse-vattery");
        assertThat(passwordEncoder.matches("correct-horse-battery", user.getPasswordHash()));
    }

    @Test
    void emailIsCaseInsensitiveAndUnique() {
        String email = "dup-" + UUID.randomUUID() + "@example.com";
        userService.register(email, "correct-horse-battery", "First");

        assertThatThrownBy(() -> userService.register(email.toUpperCase(), "another-long-password", "Second"))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
