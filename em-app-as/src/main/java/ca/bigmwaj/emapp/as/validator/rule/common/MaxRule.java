package ca.bigmwaj.emapp.as.validator.rule.common;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.BiPredicate;

@Component("MaxRule")
public class MaxRule extends AbstractLimitRule {

    private static final String MAX_PARAM = "max";
    private static final String ERROR_MSG_MAX_VALIDATION = "The value of the field must not exceed %s.";
    private static final String ERROR_MSG_STRICT_MAX_VALIDATION = "The value of the field must not strictly exceed %s.";

    @Override
    protected String getLimitParamName() {
        return MAX_PARAM;
    }

    @Override
    protected String errorLimitMsg() {
        return ERROR_MSG_MAX_VALIDATION;
    }

    @Override
    protected String errorStrictLimitMsg() {
        return ERROR_MSG_STRICT_MAX_VALIDATION;
    }

    @Override
    protected BiPredicate<BigDecimal, BigDecimal> getLimitPredicate() {
        return (a, b) -> a.compareTo(b) <= 0;
    }

    @Override
    protected BiPredicate<BigDecimal, BigDecimal> getStrictLimitPredicate() {
        return (a, b) -> a.compareTo(b) < 0;
    }
}
