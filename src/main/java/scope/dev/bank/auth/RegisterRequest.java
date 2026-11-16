package scope.dev.bank.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Email @Size(max = 254) String email,
                              @NotBlank @Size(min = 12, max = 72) String password,
                              @NotBlank @Size(max = 100) String fullName) {
    @Override
    public String toString() {
        return "RegisterRequest[email=" + email + ", password=***, fullName=" + fullName + "]";
    }
}
