package ca.bigmwaj.emapp.as.validator.rule;

import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import org.springframework.beans.BeanWrapperImpl;

@Data
public abstract class AbstractRule {

    private String message = "";

    public abstract boolean isValid(Object value);

    public boolean validate(ConstraintValidatorContext context, Object dto, String fieldName) {
        var wrapper = new BeanWrapperImpl(dto);
        var value = wrapper.getPropertyValue(fieldName);
        var isValid = isValid(value);
        if (!isValid) {
            context.buildConstraintViolationWithTemplate(String.format("The field %s is invalid", fieldName))
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
