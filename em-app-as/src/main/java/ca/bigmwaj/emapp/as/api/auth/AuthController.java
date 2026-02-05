package ca.bigmwaj.emapp.as.api.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/user")
    public UserInfo getCurrentUser(@AuthenticationPrincipal Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(authentication.getName());
        
        return userInfo;
    }

    @GetMapping("/status")
    public Map<String, Object> getAuthStatus(Authentication authentication) {
        return Map.of(
            "authenticated", authentication != null && authentication.isAuthenticated(),
            "user", authentication != null ? authentication.getName() : null
        );
    }
}
