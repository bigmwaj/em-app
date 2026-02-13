package ca.bigmwaj.emapp.as.validator.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonEmptyRule  extends AbstractRule{

    @Override
    public boolean isValid(Object value) {
        if( value == null ){
            return false;
        }
        return !((Collection<?>) value).isEmpty();
    }
}
