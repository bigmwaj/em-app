package ca.bigmwaj.emapp.as.validator.rule;

import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(setterPrefix = "with")
public class RootRule {
    private String fieldName;
    private Object dto;
    ConstraintValidatorContext context;
    @Singular
    private List<Condition.ConditionBuilder> conditions;

    public void validate(){
        conditions.stream().map(Condition.ConditionBuilder::build).forEach(
                condition -> condition.validate(context, dto, fieldName)
        );
    }
}
