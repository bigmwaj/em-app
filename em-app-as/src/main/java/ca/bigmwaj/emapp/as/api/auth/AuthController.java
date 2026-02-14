package ca.bigmwaj.emapp.as.api.auth;

import ca.bigmwaj.emapp.as.api.auth.security.JwtTokenProvider;
import ca.bigmwaj.emapp.as.dto.auth.LoginRequest;
import ca.bigmwaj.emapp.as.dto.auth.LoginResponse;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUser;
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
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for username: {}", loginRequest.getUsername());
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Extract user information
            AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                userDetails.getUsername(),
                userDetails.getUserInfo().getUsername(), // Email
                userDetails.getUserInfo().getContact() != null ? 
                    userDetails.getUserInfo().getContact().getFirstName() + " " + 
                    userDetails.getUserInfo().getContact().getLastName() : null
            );

            logger.info("Login successful for username: {}", loginRequest.getUsername());
            
            return ResponseEntity.ok(new LoginResponse(
                token,
                jwtTokenProvider.getExpirationMs(),
                userInfo
            ));
            
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for username: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
        } catch (Exception e) {
            logger.error("Error during login for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<AuthUserInfo> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthorized access attempt to /auth/user. authentication is null:{}", authentication == null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userInfo = new AuthUserInfo();
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
