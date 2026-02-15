package ca.bigmwaj.emapp.as.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for username/password authentication requests.
 * Used for traditional login (alternative to OAuth2).
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}
