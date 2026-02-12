package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.dm.dto.BaseDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class NotNullOnCreateValidator implements ConstraintValidator<ValidNotNullOnCreate, Object> {

    @Override
    public void initialize(ValidNotNullOnCreate constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        var rootBean = context.unwrap(HibernateConstraintValidatorContext.class);
        System.out.println("Root bean: " + rootBean.unwrap(BaseDto.class));
        return true;
    }
}
