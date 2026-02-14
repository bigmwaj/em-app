package ca.bigmwaj.emapp.as.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses containing JWT token.
 * Returned after successful username/password or OAuth2 authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT access token for authenticated requests
     */
    private String token;
    
    /**
     * Token type (always "Bearer" for JWT)
     */
    private String tokenType = "Bearer";
    
    /**
     * Token expiration time in milliseconds
     */
    private Long expiresIn;
    
    /**
     * Authenticated user information
     */
    private UserInfo user;
    
    public LoginResponse(String token, Long expiresIn, UserInfo user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String username;
        private String email;
        private String name;
    }
}
