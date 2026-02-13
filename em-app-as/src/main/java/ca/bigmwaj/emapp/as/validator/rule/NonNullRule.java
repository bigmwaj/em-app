package ca.bigmwaj.emapp.as.validator.rule;

import org.springframework.stereotype.Component;

@Component("NonNullRule")
public class NonNullRule extends AbstractRule {

    @Override
    public boolean isValid(Object value) {
        return value != null;
    }
}
