package ca.bigmwaj.emapp.as.validator.xml;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

/**
 * Evaluates condition expressions from XML configuration.
 * Supports simple property equality checks.
 */
@Component
public class ConditionEvaluator {

    /**
     * Evaluates a condition expression against a DTO.
     * 
     * Supported expressions:
     * - "true" - always true
     * - "fieldName == 'VALUE'" - equals check
     * - "fieldName != 'VALUE'" - not equals check
     * 
     * @param expression The condition expression
     * @param dto The DTO to evaluate against
     * @return true if condition is met, false otherwise
     */
    public boolean evaluate(String expression, Object dto) {
        if (expression == null || expression.isEmpty()) {
            return true;
        }

        expression = expression.trim();
        
        // Handle "true" literal
        if ("true".equals(expression)) {
            return true;
        }
        
        // Handle "false" literal
        if ("false".equals(expression)) {
            return false;
        }

        // Handle equality: fieldName == 'VALUE'
        if (expression.contains("==")) {
            return evaluateEquality(expression, dto, true);
        }

        // Handle inequality: fieldName != 'VALUE'
        if (expression.contains("!=")) {
            return evaluateEquality(expression, dto, false);
        }

        throw new ValidationConfigurationException("Unsupported condition expression: " + expression);
    }

    private boolean evaluateEquality(String expression, Object dto, boolean checkEquals) {
        String[] parts = expression.split(checkEquals ? "==" : "!=");
        if (parts.length != 2) {
            throw new ValidationConfigurationException("Invalid expression format: " + expression);
        }

        String fieldName = parts[0].trim();
        String expectedValue = parts[1].trim().replaceAll("^['\"]|['\"]$", ""); // Remove quotes

        try {
            BeanWrapper wrapper = new BeanWrapperImpl(dto);
            Object actualValue = wrapper.getPropertyValue(fieldName);
            
            boolean isEqual;
            if (actualValue == null) {
                isEqual = expectedValue.equals("null");
            } else {
                isEqual = actualValue.toString().equals(expectedValue);
            }

            return checkEquals ? isEqual : !isEqual;
        } catch (Exception e) {
            throw new ValidationConfigurationException(
                "Failed to evaluate condition: " + expression, e
            );
        }
    }
}
