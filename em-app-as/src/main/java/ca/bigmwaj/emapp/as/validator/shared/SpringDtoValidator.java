package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.as.validator.xml.*;
import ca.bigmwaj.emapp.as.validator.xml.model.FieldValidation;
import ca.bigmwaj.emapp.as.validator.xml.model.ValidationConfig;
import ca.bigmwaj.emapp.dm.dto.BaseDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spring-based DTO validator that loads validation rules from XML configuration.
 * Uses the namespace from @ValidDto annotation to locate and parse XML validation files.
 */
@Component
public class SpringDtoValidator implements ConstraintValidator<ValidDto, Object> {

    private static final Logger logger = LoggerFactory.getLogger(SpringDtoValidator.class);

    @Autowired
    private ValidationXmlParser xmlParser;

    @Autowired
    private RuleFactory ruleFactory;

    @Autowired
    private ConditionEvaluator conditionEvaluator;

    private String namespace;

    private ValidationConfig getValidationConfig() {
        try {
            return xmlParser.getValidationConfig(this.namespace);
        } catch (Exception e) {
            logger.error("Error loading validation configuration for namespace: {}", namespace, e);
            throw new ValidationConfigurationException("Failed to load validation configuration", e);
        }
    }

    @Override
    public void initialize(ValidDto constraintAnnotation) {
        this.namespace = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null checks
        }

        try {
            var config = getValidationConfig();

            // Disable default constraint violation
            context.disableDefaultConstraintViolation();

            // Execute validation for each field
            var isValid = true;
            for (var fieldValidation : config.getFields()) {
                boolean fieldValid = validateField(dto, fieldValidation, context);
                if (!fieldValid) {
                    isValid = false;
                }
            }

            return isValid;

        } catch (ValidationConfigurationException e) {
            logger.error("Validation configuration error for namespace: {}", namespace, e);
            // In case of configuration error, fail gracefully
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during validation for namespace: {}", namespace, e);
            return false;
        }
    }

    private boolean validateField(Object dto, FieldValidation fieldValidation, ConstraintValidatorContext context) {
        boolean isValid = true;

        for (var conditionConfig : fieldValidation.getConditions()) {
            logger.debug("Le type du dto est : {}", dto.getClass());
            logger.debug("Le nom du champ est : {}", fieldValidation.getName());
            // Evaluate condition
            var conditionMet = conditionEvaluator.evaluate(conditionConfig.getExpression(), dto);
            if (conditionMet) {
                isValid = conditionConfig.getRules().stream()
                        .map(ruleFactory::createRule)
                        .allMatch(r -> r.validate(context, dto, fieldValidation.getName()));
            }
        }

        if( isValid && fieldValidation.getValidationConfig() != null){
            var nestedConfig = fieldValidation.getValidationConfig();
            var wrapper = new BeanWrapperImpl(dto);
            var nestedObject = wrapper.getPropertyValue(fieldValidation.getName());
            if( nestedObject != null ){
                if( nestedObject instanceof BaseDto ){
                    isValid = nestedConfig.getFields().stream()
                            .allMatch(fv -> validateField(nestedObject, fv, context));
                }else{
                    for(var nestedObjectItem : (Iterable<?>) nestedObject){
                        isValid = nestedConfig.getFields().stream()
                                .allMatch(fv -> validateField(nestedObjectItem, fv, context));
                        if( !isValid ){
                            break;
                        }
                    }
                }
            }
        }
        return isValid;
    }
}
