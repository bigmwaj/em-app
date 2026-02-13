package ca.bigmwaj.emapp.as.validator.rule;

import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("NonEmptyRule")
public class NonEmptyRule extends AbstractRule {

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return false;
        }
        return !((Collection<?>) value).isEmpty();
    }
}
