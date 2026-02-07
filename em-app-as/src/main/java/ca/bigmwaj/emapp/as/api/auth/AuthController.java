package ca.bigmwaj.emapp.as.api.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/user")
    public ResponseEntity<UserInfo> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthorized access attempt to /auth/user. authentication is null:{}", authentication == null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.debug("Preparing user info for authenticated user: {}, Principal: {}", authentication.getName(), authentication.getClass());

        UserInfo userInfo = new UserInfo();
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
