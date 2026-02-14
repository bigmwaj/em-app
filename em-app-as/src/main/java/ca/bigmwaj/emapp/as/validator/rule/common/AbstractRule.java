package ca.bigmwaj.emapp.as.validator.rule.common;

import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Map;

@Data
public abstract class AbstractRule {

    private static final String MSG_FIELD_INVALID = "The field %s is invalid";

    public abstract boolean isValid(Object value, Map<String, String> parameters);

    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        if (parameters != null && parameters.containsKey("message")) {
            return parameters.get("message");
        }
        return String.format(MSG_FIELD_INVALID, fieldName);
    }

    public boolean validate(ConstraintValidatorContext context, Object dto, String fieldName, Map<String, String> parameters) {
        BeanWrapper wrapper = new BeanWrapperImpl(dto);
        Object value = wrapper.getPropertyValue(fieldName);
        boolean isValid = isValid(value, parameters);

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(getErrorMessage(fieldName, value, parameters))
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
