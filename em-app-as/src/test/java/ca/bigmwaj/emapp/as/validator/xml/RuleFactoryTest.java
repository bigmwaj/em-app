package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.rule.AbstractRule;
import ca.bigmwaj.emapp.as.validator.rule.MaxLengthRule;
import ca.bigmwaj.emapp.as.validator.rule.NonEmptyRule;
import ca.bigmwaj.emapp.as.validator.rule.NonNullRule;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RuleFactoryTest {

    @Autowired
    private RuleFactory ruleFactory;

    @Test
    void testCreateNonNullRule() {
        RuleConfig config = new RuleConfig();
        config.setType("NonNullRule");

        AbstractRule rule = ruleFactory.createRule(config);

        assertNotNull(rule);
        assertInstanceOf(NonNullRule.class, rule);
    }

    @Test
    void testCreateNonEmptyRule() {
        RuleConfig config = new RuleConfig();
        config.setType("NonEmptyRule");

        AbstractRule rule = ruleFactory.createRule(config);

        assertNotNull(rule);
        assertInstanceOf(NonEmptyRule.class, rule);
    }

    @Test
    void testCreateMaxLengthRule() {
        RuleConfig config = new RuleConfig();
        config.setType("MaxLengthRule");
        config.getParameters().put("maxLength", "50");

        AbstractRule rule = ruleFactory.createRule(config);

        assertNotNull(rule);
        assertInstanceOf(MaxLengthRule.class, rule);
        assertEquals(50, ((MaxLengthRule) rule).getMaxLength());
    }

    @Test
    void testCreateMaxLengthRule_MissingParameter() {
        RuleConfig config = new RuleConfig();
        config.setType("MaxLengthRule");

        assertThrows(ValidationConfigurationException.class, () -> {
            ruleFactory.createRule(config);
        });
    }

    @Test
    void testCreateMaxLengthRule_InvalidParameter() {
        RuleConfig config = new RuleConfig();
        config.setType("MaxLengthRule");
        config.getParameters().put("maxLength", "invalid");

        assertThrows(ValidationConfigurationException.class, () -> {
            ruleFactory.createRule(config);
        });
    }

    @Test
    void testCreateUnknownRule() {
        RuleConfig config = new RuleConfig();
        config.setType("UnknownRule");

        assertThrows(ValidationConfigurationException.class, () -> {
            ruleFactory.createRule(config);
        });
    }
}
