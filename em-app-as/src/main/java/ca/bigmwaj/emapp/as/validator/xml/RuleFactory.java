package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.rule.AbstractRule;
import ca.bigmwaj.emapp.as.validator.rule.MaxLengthRule;
import ca.bigmwaj.emapp.as.validator.rule.NonEmptyRule;
import ca.bigmwaj.emapp.as.validator.rule.NonNullRule;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import org.springframework.stereotype.Component;

/**
 * Factory for creating rule instances from XML configuration.
 */
@Component
public class RuleFactory {

    /**
     * Creates a rule instance from XML configuration.
     * 
     * @param ruleConfig The rule configuration from XML
     * @return AbstractRule instance
     * @throws ValidationConfigurationException if rule type is unknown
     */
    public AbstractRule createRule(RuleConfig ruleConfig) {
        String ruleType = ruleConfig.getType();
        
        if (ruleType == null || ruleType.isEmpty()) {
            throw new ValidationConfigurationException("Rule type cannot be null or empty");
        }

        return switch (ruleType) {
            case "NonNullRule" -> new NonNullRule();
            case "NonEmptyRule" -> new NonEmptyRule();
            case "MaxLengthRule" -> createMaxLengthRule(ruleConfig);
            default -> throw new ValidationConfigurationException("Unknown rule type: " + ruleType);
        };
    }

    private MaxLengthRule createMaxLengthRule(RuleConfig ruleConfig) {
        String maxLengthStr = ruleConfig.getParameters().get("maxLength");
        if (maxLengthStr == null || maxLengthStr.isEmpty()) {
            throw new ValidationConfigurationException("MaxLengthRule requires 'maxLength' parameter");
        }

        try {
            int maxLength = Integer.parseInt(maxLengthStr);
            return new MaxLengthRule(maxLength);
        } catch (NumberFormatException e) {
            throw new ValidationConfigurationException(
                "Invalid maxLength value for MaxLengthRule: " + maxLengthStr, e
            );
        }
    }
}
