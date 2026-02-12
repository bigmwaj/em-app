package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.dm.dto.BaseDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.List;

public class NotEmptyOnCreateValidator implements ConstraintValidator<ValidNotEmptyOnCreate, List<? extends BaseDto>> {

    @Override
    public void initialize(ValidNotEmptyOnCreate constraintAnnotation) {

    }

    @Override
    public boolean isValid(List<? extends BaseDto> dtos, ConstraintValidatorContext context) {
        Object rootBean = context.unwrap(BaseDto.class);
        System.out.println("Root bean: " + rootBean);
        
        return true;
    }
}
