package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.service.platform.UserService;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UniqueUsernameRule")
public class UniqueUsernameRule extends AbstractRule {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(Object username, Map<String, String> parameters) {
        String _username = (String) username;
        if (_username == null || _username.isBlank()) {
            return true; // Let @NotBlank handle this
        }
        return userService.isUsernameUnique(_username);
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return String.format("The username '%s' is already taken. Please choose a different username.", value);
    }
}
