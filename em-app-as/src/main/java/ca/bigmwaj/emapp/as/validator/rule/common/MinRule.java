package ca.bigmwaj.emapp.as.validator.rule.common;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.BiPredicate;

@Component("MinRule")
public class MinRule extends AbstractLimitRule {

    private static final String MIN_PARAM = "min";
    private static final String ERROR_MSG_MIN_VALIDATION = "The value of the field must be at least %s.";
    private static final String ERROR_MSG_STRICT_MIN_VALIDATION = "The value of the field must be strictly at least %s.";

    @Override
    protected String getLimitParamName() {
        return MIN_PARAM;
    }

    @Override
    protected String errorLimitMsg() {
        return ERROR_MSG_MIN_VALIDATION;
    }

    @Override
    protected String errorStrictLimitMsg() {
        return ERROR_MSG_STRICT_MIN_VALIDATION;
    }

    @Override
    protected BiPredicate<BigDecimal, BigDecimal> getLimitPredicate() {
        return (a, b) -> a.compareTo(b) >= 0;
    }

    @Override
    protected BiPredicate<BigDecimal, BigDecimal> getStrictLimitPredicate() {
        return (a, b) -> a.compareTo(b) > 0;
    }
}
