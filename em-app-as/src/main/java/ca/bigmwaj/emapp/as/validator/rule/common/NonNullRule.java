package ca.bigmwaj.emapp.as.validator.rule.common;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component("NonNullRule")
public class NonNullRule extends AbstractRule {

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        return value != null;
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return String.format("The field '%s' cannot be null.", fieldName);
    }
}
