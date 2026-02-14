package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.rule.common.MaxLengthRule;
import ca.bigmwaj.emapp.as.validator.rule.common.NonEmptyRule;
import ca.bigmwaj.emapp.as.validator.rule.common.NonNullRule;
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
        var config = new RuleConfig();
        config.setType("NonNullRule");

        AbstractRule rule = ruleFactory.createRule(config);

        assertNotNull(rule);
        assertInstanceOf(NonNullRule.class, rule);
    }

    @Test
    void testCreateNonEmptyRule() {
        var config = new RuleConfig();
        config.setType("NonEmptyRule");

        AbstractRule rule = ruleFactory.createRule(config);

        assertNotNull(rule);
        assertInstanceOf(NonEmptyRule.class, rule);
    }

    @Test
    void testCreateMaxLengthRule() {
        var config = new RuleConfig();
        config.setType("MaxLengthRule");
        config.getParameters().put("maxLength", "50");

        AbstractRule rule = ruleFactory.createRule(config);

        assertNotNull(rule);
        assertInstanceOf(MaxLengthRule.class, rule);
//        assertEquals(50, ((MaxLengthRule) rule).getMaxLength());
    }

    @Test
    void testCreateUnknownRule() {
        var config = new RuleConfig();
        config.setType("UnknownRule");

        assertThrows(ValidationConfigurationException.class, () -> {
            ruleFactory.createRule(config);
        });
    }
}
