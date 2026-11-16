package scope.dev.bank.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import scope.dev.bank.user.User;
import scope.dev.bank.user.UserResponse;
import scope.dev.bank.user.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.email(), request.password(), request.fullName());
        return UserResponse.from(user);
    }
}
