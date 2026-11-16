package scope.dev.bank.user;

import java.util.UUID;

public record UserResponce(UUID id, String email, String fullName) {
    public static UserResponce from(User user) {
        
    }
}
