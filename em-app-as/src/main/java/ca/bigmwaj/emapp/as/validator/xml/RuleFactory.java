package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.rule.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
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

    public AbstractRule getBeanByName(String beanName) {
        return applicationContext.getBean(beanName, AbstractRule.class);
    }

    /**
     * Creates a rule instance from XML configuration.
     *
     * @param ruleConfig The rule configuration from XML
     * @return AbstractRule instance
     * @throws ValidationConfigurationException if rule type is unknown
     */
    public AbstractRule createRule(RuleConfig ruleConfig) {
        try {
            String ruleType = ruleConfig.getType();
            var rule = getBeanByName(ruleType);
            if (ruleConfig.getParameters() != null && !ruleConfig.getParameters().isEmpty()) {
                var wrapper = new BeanWrapperImpl(rule);
                ruleConfig.getParameters().forEach((key, value) -> {
                    if (wrapper.isWritableProperty(key)) {
                        wrapper.setPropertyValue(key, value);
                    } else {
                        logger.error("Unknown property '{}' for rule type '{}'", key, ruleType);
                    }
                });
            }
            return rule;
        } catch (Exception e) {
            throw new ValidationConfigurationException("Failed to create rule of type: " + ruleConfig.getType(), e);
        }
    }
}
