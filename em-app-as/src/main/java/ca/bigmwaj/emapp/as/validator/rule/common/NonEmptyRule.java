package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component("NonEmptyRule")
public class NonEmptyRule extends AbstractRule {

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if (value == null) {
            return false;
        }

        if( value instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }else{
            throw new ValidationConfigurationException("NonEmptyRule only supports Collection types");
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return String.format("The field '%s' must not be empty.", fieldName);
    }
}
