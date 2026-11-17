package scope.dev.bank.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
