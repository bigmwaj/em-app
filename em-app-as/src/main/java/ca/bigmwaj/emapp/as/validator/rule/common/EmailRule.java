package ca.bigmwaj.emapp.as.validator.rule.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component("EmailRule")
public class EmailRule extends AbstractRule {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if (value == null) {
            return true;
        }

        String email = value.toString().trim();
        return pattern.matcher(email).matches();
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "The field '%s' must be a valid email".formatted(fieldName);
    }
}
