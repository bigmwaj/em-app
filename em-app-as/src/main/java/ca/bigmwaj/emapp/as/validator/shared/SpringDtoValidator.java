package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.as.validator.rule.AbstractRule;
import ca.bigmwaj.emapp.as.validator.rule.Condition;
import ca.bigmwaj.emapp.as.validator.rule.RootRule;
import ca.bigmwaj.emapp.as.validator.xml.*;
import ca.bigmwaj.emapp.as.validator.xml.model.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring-based DTO validator that loads validation rules from XML configuration.
 * Uses the namespace from @ValidDto annotation to locate and parse XML validation files.
 */
@Component
public class SpringDtoValidator implements ConstraintValidator<ValidDto, Object> {

    private static final Logger logger = LoggerFactory.getLogger(SpringDtoValidator.class);

    @Autowired
    private ValidationNamespaceResolver namespaceResolver;

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

        logger.debug("Validating DTO with namespace: {}", namespace);

        try {
            // Resolve namespace to XML file
            InputStream xmlStream = namespaceResolver.resolveNamespace(namespace);
            String entryPoint = namespaceResolver.extractEntryPoint(namespace);

            // Parse XML configuration
            ValidationConfig config = xmlParser.parse(xmlStream);

            // Find the matching entry
            ValidationEntry entry = findEntry(config, entryPoint);
            if (entry == null) {
                logger.warn("No validation entry found for namespace: {} with entry point: {}", namespace, entryPoint);
                return true; // No validation configured
            }

            // Disable default constraint violation
            context.disableDefaultConstraintViolation();

            // Execute validation for each field
            boolean isValid = true;
            for (FieldValidation fieldValidation : entry.getFields()) {
                boolean fieldValid = validateField(dto, fieldValidation, context);
                if (!fieldValid) {
                    isValid = false;
                }
            }

            return isValid;

        } catch (ValidationConfigurationException e) {
            logger.error("Validation configuration error for namespace: {}", namespace, e);
            // In case of configuration error, fail gracefully
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error during validation for namespace: {}", namespace, e);
            return true;
        }
    }

    private ValidationEntry findEntry(ValidationConfig config, String entryPoint) {
        return config.getEntries().stream()
            .filter(entry -> entryPoint.equals(entry.getName()))
            .findFirst()
            .orElse(null);
    }

    private boolean validateField(Object dto, FieldValidation fieldValidation, ConstraintValidatorContext context) {
        boolean isValid = true;

        for (ConditionConfig conditionConfig : fieldValidation.getConditions()) {
            // Evaluate condition
            boolean conditionMet = conditionEvaluator.evaluate(conditionConfig.getExpression(), dto);

            if (conditionMet) {
                // Build rules
                List<AbstractRule> rules = conditionConfig.getRules().stream()
                    .map(ruleFactory::createRule)
                    .collect(Collectors.toList());

                // Create condition builder
                Condition.ConditionBuilder conditionBuilder = Condition.builder()
                    .withCondition(true);
                
                // Add all rules to the builder
                for (AbstractRule rule : rules) {
                    conditionBuilder.withRule(rule);
                }

                // Create and execute validation
                RootRule rootRule = RootRule.builder()
                    .withFieldName(fieldValidation.getName())
                    .withDto(dto)
                    .withContext(context)
                    .withCondition(conditionBuilder)
                    .build();

                try {
                    rootRule.validate();
                } catch (Exception e) {
                    logger.error("Error validating field: {}", fieldValidation.getName(), e);
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}
