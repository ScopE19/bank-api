package scope.dev.bank.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String email, String rawPassword, String fullName) {
        String normalizedEmail = normalize(email);
        if (users.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException();
        }
        return users.save(new User(normalizedEmail, passwordEncoder.encode(rawPassword), fullName));
    }

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
