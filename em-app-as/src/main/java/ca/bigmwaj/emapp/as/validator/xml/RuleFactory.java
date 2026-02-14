package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Factory for creating rule instances from XML configuration.
 */
@Component
public class RuleFactory {

    private static final Logger logger = LoggerFactory.getLogger(RuleFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Creates a rule instance from XML configuration.
     *
     * @param ruleConfig The rule configuration from XML
     * @return AbstractRule instance
     * @throws ValidationConfigurationException if rule type is unknown
     */
    public AbstractRule createRule(RuleConfig ruleConfig) {
        try {
            return applicationContext.getBean(ruleConfig.getType(), AbstractRule.class);
        } catch (Exception e) {
            throw new ValidationConfigurationException("Failed to create rule of type: " + ruleConfig.getType(), e);
        }
    }
}
