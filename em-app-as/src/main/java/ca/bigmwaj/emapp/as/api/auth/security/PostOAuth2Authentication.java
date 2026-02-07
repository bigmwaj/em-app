package ca.bigmwaj.emapp.as.api.auth.security;

import ca.bigmwaj.emapp.as.service.platform.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostOAuth2Authentication implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(PostOAuth2Authentication.class);
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        validateProvider(userRequest);
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_missing"),
                    "Email not provided by OAuth2 provider"
            );
        }
        userService.validateAccountHolder(email);
        return oAuth2User;
    }

    private void validateProvider(OAuth2UserRequest request) {
        String registrationId = request.getClientRegistration().getRegistrationId();

        if (!List.of("google", "github", "facebook").contains(registrationId)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_provider"),
                    "OAuth2 provider not allowed"
            );
        }
    }
}
