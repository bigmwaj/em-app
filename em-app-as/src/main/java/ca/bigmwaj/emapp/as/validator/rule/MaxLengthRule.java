package ca.bigmwaj.emapp.as.validator.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MaxLengthRule extends AbstractRule {
    private int maxLength;

    @Override
    public boolean isValid(Object value) {
        if( value == null ){
            return true; // Let @NotNull handle this
        }
        if( value instanceof String ){
            return ((String) value).length() <= maxLength;
        }
        return false;
    }
}
