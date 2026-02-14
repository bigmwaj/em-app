package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("MaxLengthRule")
public class MaxLengthRule extends AbstractRule {

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {

        if (value == null) {
            return true; // Let @NotNull handle this
        }

        if (value instanceof String) {
            int maxLength;
            if (parameters.containsKey("maxLength")) {
                maxLength = Integer.parseInt(parameters.get("maxLength"));
            } else {
                throw new ValidationConfigurationException("MaxLengthRule requires a maxLength parameter");
            }

            return ((String) value).length() <= maxLength;
        }
        return false;
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return String.format("The field '%s' must not exceed %s characters in length.", fieldName, parameters.get("maxLength"));
    }
}
