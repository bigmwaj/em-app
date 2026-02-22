package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.BiPredicate;

public abstract class AbstractLimitRule extends AbstractRule {

    private static final String STRICT_PARAM = "strict";
    private static final String ERROR_MSG_LIMIT_REQUIRED = "%s requires a %s parameter";
    private static final String ERROR_MSG_LIMIT_VALIDATION = "The value of the field '%s' is not valid.";

    abstract protected String getLimitParamName();

    abstract protected String errorLimitMsg();

    abstract protected String errorStrictLimitMsg();

    abstract protected BiPredicate<BigDecimal, BigDecimal> getLimitPredicate();

    abstract protected BiPredicate<BigDecimal, BigDecimal> getStrictLimitPredicate();

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {

        if (value == null) {
            return true;
        }

        try {
            BigDecimal val = new BigDecimal(value.toString());
            BigDecimal limit;
            if (parameters.containsKey(getLimitParamName())) {
                limit = new BigDecimal(parameters.get(getLimitParamName()));
            } else {
                throw new ValidationConfigurationException(ERROR_MSG_LIMIT_REQUIRED.formatted(this.getClass().getSimpleName(), getLimitParamName()));
            }

            boolean strict = false;
            if (parameters.containsKey(STRICT_PARAM)) {
                strict = Boolean.parseBoolean(parameters.get(STRICT_PARAM));
            }

            boolean isValid;
            if (strict) {
                isValid = getStrictLimitPredicate().test(BigDecimal.valueOf(val.doubleValue()), limit);
                if(!isValid){
                    parameters.put("message", errorStrictLimitMsg().formatted(limit));
                }
            }else {
                isValid = getLimitPredicate().test(BigDecimal.valueOf(val.doubleValue()), limit);
                if(!isValid){
                    parameters.put("message", errorLimitMsg().formatted(limit));
                }
            }

            return isValid;
        }catch (ValidationConfigurationException e){
            throw e;
        }catch (Exception e){
            throw new ValidationConfigurationException("Unable to validation the field", e);
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        if (parameters != null && parameters.containsKey("message")) {
            return parameters.get("message");
        }else{
            return ERROR_MSG_LIMIT_VALIDATION.formatted(fieldName);
        }
    }
}
