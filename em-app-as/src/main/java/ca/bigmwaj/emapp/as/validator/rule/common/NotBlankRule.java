package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("NotBlankRule")
public class NotBlankRule extends AbstractRule {

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if( value == null) {
            return false;
        }

        if (value instanceof String str) {
            return !str.isBlank();
        } else {
            throw new ValidationConfigurationException("NotBlankRule only supports String types");
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return String.format("The field '%s' must not be blank.", fieldName);
    }
}
