package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.RuleFactory;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AbstractRuleTest {

    @Autowired
    protected RuleFactory ruleFactory;

    protected AbstractRule rule;

    protected RuleConfig initRuleConfig(String type){
        var config = new RuleConfig();
        config.setType(type);
        return config;
    }
}
