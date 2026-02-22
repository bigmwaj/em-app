package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("EqualsRule")
public class EqualsRule extends AbstractRule {

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if (value == null) {
            return true;
        }

        if (parameters.containsKey("value")) {
            var val = parameters.get("value");
            if (value instanceof String str) {
                return str.equals(val);
            }

            if (value instanceof Number num) {
                try {
                    double doubleVal = Double.parseDouble(val);
                    return num.doubleValue() == doubleVal;
                } catch (NumberFormatException e) {
                    throw new ValidationConfigurationException("EqualsRule value parameter must be a valid number for Number types");
                }
            }

            if( value instanceof Boolean bool){
                if( val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")){
                    return bool == Boolean.parseBoolean(val);
                }else{
                    throw new ValidationConfigurationException("EqualsRule value parameter must be a valid boolean for Boolean types");
                }
            }

            if( value instanceof Enum<?> enumValue){
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) enumValue.getClass();
                    Enum<?> enumVal = toEnum(enumClass, val);
                    return enumValue.equals(enumVal);
                } catch (IllegalArgumentException e) {
                    throw new ValidationConfigurationException("EqualsRule value parameter must be a valid enum constant for Enum types");
                }
            }

            throw new ValidationConfigurationException("EqualsRule value parameter must be a valid number for String types");

        } else {
            throw new ValidationConfigurationException("EqualsRule requires a value parameter");
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return String.format("The field '%s' must be equal %s.", fieldName, parameters.get("value"));
    }

    private static <E extends Enum<E>> E toEnum(Class<?> enumType, String value) {
        // Runtime check to ensure type safety before casting
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new IllegalArgumentException("Type must be an Enum type: " + enumType.getName());
        }
        return Enum.valueOf((Class<E>) enumType, value);
    }
}
