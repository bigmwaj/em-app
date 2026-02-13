package ca.bigmwaj.emapp.as.validator.shared;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringDtoValidator implements ConstraintValidator<ValidDto, Object> {

    private static final Logger logger = LoggerFactory.getLogger(SpringDtoValidator.class);

    private String namespace;

    @Override
    public void initialize(ValidDto constraintAnnotation) {
        this.namespace = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        logger.debug("Let's validate the DTO based on the namespace: {}", namespace);
        return true;
    }
}
