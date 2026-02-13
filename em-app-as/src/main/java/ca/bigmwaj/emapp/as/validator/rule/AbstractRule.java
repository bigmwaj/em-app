package ca.bigmwaj.emapp.as.validator.rule;

import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Data
public abstract class AbstractRule {

    public abstract boolean isValid(Object value);

    public void validate(ConstraintValidatorContext context, Object dto, String fieldName) {
        BeanWrapper wrapper = new BeanWrapperImpl(dto);
        Object value = wrapper.getPropertyValue(fieldName);
        if (!isValid(value)) {
            context.buildConstraintViolationWithTemplate(String.format("The field %s is invalid", fieldName))
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
        }
    }

}
