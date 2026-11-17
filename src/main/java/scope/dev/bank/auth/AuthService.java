package scope.dev.bank.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scope.dev.bank.user.User;
import scope.dev.bank.user.UserRepository;
import scope.dev.bank.user.UserService;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String dummyHash;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.dummyHash = passwordEncoder.encode("not-a-real-password");
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String email, String rawPassword) {
        User user = users.findByEmail(UserService.normalize(email)).orElse(null);

        String hash = (user != null) ? user.getPasswordHash() : dummyHash;
        boolean passwordMatches = passwordEncoder.matches(rawPassword, hash);

        if (user == null || !passwordMatches) {
            throw new InvalidCredentialsException();
        }
        return tokenService.issueFor(user);
    }
}
