package ca.bigmwaj.emapp.as.api.auth;

import ca.bigmwaj.emapp.as.api.auth.security.JwtTokenProvider;
import ca.bigmwaj.emapp.as.dto.auth.ErrorResponse;
import ca.bigmwaj.emapp.as.dto.auth.LoginRequest;
import ca.bigmwaj.emapp.as.dto.auth.LoginResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 * Handles both OAuth2 and username/password authentication.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticates user with username and password.
     * Returns JWT token on successful authentication.
     *
     * @param loginRequest the login credentials
     * @return JWT token and user information or error response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for username: {}", loginRequest.getUsername());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getUsername(), loginRequest.getPassword()));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new LoginResponse(
                    token,
                    jwtTokenProvider.getExpirationMs()
            ));
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for username: {}", loginRequest.getUsername());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid username or password"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error during login for username: {}", loginRequest.getUsername(), e);
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An error occurred during authentication. Please try again later."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<AuthUserInfo> getCurrentUser(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthorized access attempt to /auth/user. authentication is null:{}", authentication == null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthUserInfo userInfo = new AuthUserInfo();
        userInfo.setName(authentication.getName());
        userInfo.setEmail(authentication.getName());

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/status")
    public Map<String, Object> getAuthStatus(Authentication authentication) {
        return Map.of(
                "authenticated", authentication != null && authentication.isAuthenticated(),
                "user", authentication != null ? authentication.getName() : null
        );
    }
}
