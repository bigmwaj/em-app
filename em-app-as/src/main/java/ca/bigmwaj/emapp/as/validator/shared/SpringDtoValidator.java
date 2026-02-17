package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.as.validator.ValidationException;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ConditionEvaluator;
import ca.bigmwaj.emapp.as.validator.xml.RuleFactory;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import ca.bigmwaj.emapp.as.validator.xml.ValidationXmlParser;
import ca.bigmwaj.emapp.as.validator.xml.model.FieldValidation;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import ca.bigmwaj.emapp.as.validator.xml.model.ValidationConfig;
import ca.bigmwaj.emapp.dm.dto.AbstractChangeTrackingDto;
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

    @Override
    public void initialize(ValidDto constraintAnnotation) {
        this.namespace = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null checks
        }

        if (!(dto instanceof AbstractChangeTrackingDto baseDto)) {
            throw new ValidationException("DTO must extend BaseDto for validation");
        }

        if (baseDto.getEditAction() == null) {
            throw new ValidationException("DTO must have an edit action specified for validation");
        }

        try {
            var config = getValidationConfig();

            // Disable default constraint violation
            //context.disableDefaultConstraintViolation();

            // Execute validation for each field

            var isValid = true;
            for (var fieldValidation : config.getFields()) {
                boolean validField = validateField(dto, fieldValidation, context);
                isValid = isValid && validField;
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

    private ValidationConfig getValidationConfig() {
        try {
            return xmlParser.getValidationConfig(this.namespace);
        } catch (Exception e) {
            logger.error("Error loading validation configuration for namespace: {}", namespace, e);
            throw new ValidationConfigurationException("Failed to load validation configuration", e);
        }
    }

    private boolean validateField(Object dto, FieldValidation fieldValidation, ConstraintValidatorContext context) {
        boolean isValid = true;

        for (var conditionConfig : fieldValidation.getConditions()) {
            // Evaluate condition
            var conditionMet = conditionEvaluator.evaluate(conditionConfig.getExpression(), dto);
            if (conditionMet) {
                isValid = conditionConfig.getRules().stream()
                        .allMatch(rc -> validate(rc, context, dto, fieldValidation.getName()));
            }
        }

        if (isValid && fieldValidation.getValidationConfig() != null) {
            var nestedConfig = fieldValidation.getValidationConfig();
            var wrapper = new BeanWrapperImpl(dto);
            var nestedObject = wrapper.getPropertyValue(fieldValidation.getName());
            if (nestedObject != null) {
                if (nestedObject instanceof AbstractChangeTrackingDto) {
                    isValid = nestedConfig.getFields().stream()
                            .allMatch(fv -> validateField(nestedObject, fv, context));
                } else {
                    for (var nestedObjectItem : (Iterable<?>) nestedObject) {
                        isValid = nestedConfig.getFields().stream()
                                .allMatch(fv -> validateField(nestedObjectItem, fv, context));

                        if (!isValid) {
                            break;
                        }
                    }
                }
            }
        }
        return isValid;
    }

    public boolean validate(RuleConfig ruleConfig, ConstraintValidatorContext context, Object dto, String fieldName) {
        AbstractRule rule = ruleFactory.createRule(ruleConfig);
        return rule.validate(context, dto, fieldName, ruleConfig.getParameters());
    }
}
