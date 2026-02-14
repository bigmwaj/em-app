package ca.bigmwaj.emapp.as.api.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token from an Authentication object.
     * Supports both OAuth2 and username/password authentication.
     * 
     * @param authentication the authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        var now = new Date();
        var expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        String subject;
        String name = null;
        String email = null;
        String provider = "local";
        
        // Handle OAuth2 authentication
        if (authentication instanceof OAuth2AuthenticationToken) {
            var auth = (OAuth2AuthenticationToken) authentication;
            var userPrincipal = (OAuth2User) authentication.getPrincipal();
            
            email = userPrincipal.getAttribute("email");
            name = userPrincipal.getAttribute("name");
            provider = auth.getAuthorizedClientRegistrationId();
            subject = email != null ? email : userPrincipal.getName();
        } 
        // Handle username/password authentication
        else {
            var principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                subject = userDetails.getUsername();
                // Try to extract additional info if available
                if (userDetails instanceof ca.bigmwaj.emapp.as.dto.security.AuthenticatedUser) {
                    var authUser = (ca.bigmwaj.emapp.as.dto.security.AuthenticatedUser) userDetails;
                    var userDto = authUser.getUserInfo();
                    if (userDto != null) {
                        email = userDto.getUsername(); // Username is typically email
                        // Name could be extracted from contact if available
                        if (userDto.getContact() != null) {
                            name = userDto.getContact().getFirstName() + " " + userDto.getContact().getLastName();
                        }
                    }
                }
            } else {
                subject = authentication.getName();
            }
        }

        return Jwts.builder()
                .subject(subject)
                .claim("name", name)
                .claim("email", email)
                .claim("provider", provider)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Generates a JWT token for a specific username (for username/password login).
     * 
     * @param username the username
     * @param email the user's email
     * @param name the user's full name
     * @return JWT token string
     */
    public String generateTokenForUser(String username, String email, String name) {
        var now = new Date();
        var expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("name", name)
                .claim("email", email)
                .claim("provider", "local")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public String getAuthorizedClientRegistrationIdFromJWT(String token) {
        var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        // Try to get provider claim (new format)
        Object provider = claims.get("provider");
        if (provider != null) {
            return provider.toString();
        }
        
        // Fallback to old format for backward compatibility
        Object registrationId = claims.get("authorizedClientRegistrationId");
        if (registrationId != null) {
            return registrationId.toString();
        }
        
        return "local"; // Default for username/password auth
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (Exception ex) {
            logger.debug("Invalid JWT token", ex);
        }
        return false;
    }
    
    /**
     * Gets the JWT expiration time in milliseconds.
     * 
     * @return expiration time in milliseconds
     */
    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}
