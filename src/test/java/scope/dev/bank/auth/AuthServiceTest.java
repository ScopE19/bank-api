package scope.dev.bank.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;
import scope.dev.bank.TestcontainersConfiguration;
import scope.dev.bank.user.User;
import scope.dev.bank.user.UserService;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class AuthServiceTest {
    @Autowired
    UserService userService;
    @Autowired AuthService authService;
    @Autowired JwtDecoder jwtDecoder;

    @Test
    void loginReturnsVerifiableTokenForTheUser() {
        String email = "login-" + UUID.randomUUID() + "@example.com";
        User user = userService.register(email, "correct-horse-battery", "Alice");

        TokenResponse response = authService.login(email.toUpperCase(), "correct-horse-battery");

        Jwt jwt = jwtDecoder.decode(response.accessToken());
        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
        assertThat(jwt.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void wrongPasswordAndUnknownEmailLookIdentical() {
        String email = "login-" + UUID.randomUUID() + "@example.com";
        userService.register(email, "correct-horse-battery", "Alice");

        assertThatThrownBy(() -> authService.login(email, "wrong-password-123"))
                .isInstanceOf(InvalidCredentialsException.class).hasMessage("Invalid email or password");
        assertThatThrownBy(() -> authService.login("nobody@example.com", "whatever-password"))
                .isInstanceOf(InvalidCredentialsException.class).hasMessage("Invalid email or password");
    }

    @Test
    void decoderRejectsTamperedToken() {
        String email = "login-" + UUID.randomUUID() + "@example.com";
        userService.register(email, "correct-horse-battery", "Alice");
        String token = authService.login(email, "correct-horse-battery").accessToken();
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        assertThatThrownBy(() -> jwtDecoder.decode(tampered)).isInstanceOf(JwtException.class);
    }
}
