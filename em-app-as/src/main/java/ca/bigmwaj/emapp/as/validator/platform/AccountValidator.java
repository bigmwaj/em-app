package ca.bigmwaj.emapp.as.validator.platform;

import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.validator.rule.Condition;
import ca.bigmwaj.emapp.as.validator.rule.MaxLengthRule;
import ca.bigmwaj.emapp.as.validator.rule.NonNullRule;
import ca.bigmwaj.emapp.as.validator.rule.RootRule;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintValidatorContext;

public class AccountValidator extends AbstractValidator<ValidAccount, AccountDto> {

    @Override
    public boolean isValid(AccountDto dto, ConstraintValidatorContext context) {
        if (dto.getEditAction() == null) {
            return false;
        }

        boolean isCreate = EditActionLvo.CREATE.equals(dto.getEditAction());
        boolean isUpdate = EditActionLvo.UPDATE.equals(dto.getEditAction());
        boolean isChangeStatus = EditActionLvo.CHANGE_STATUS.equals(dto.getEditAction());
        boolean isCreateOrUpdate = isCreate || isUpdate;

        RootRule.builder()
                .withFieldName("id")
                .withDto(dto)
                .withContext(context)
                .withCondition(
                        Condition.builder().withCondition(isUpdate || isChangeStatus)
                                .withRule(new NonNullRule())
                                .withRule(new MaxLengthRule(256))
                )
                .build().validate();

        RootRule.builder()
                .withFieldName("name")
                .withDto(dto)
                .withContext(context)
                .withCondition(
                        Condition.builder().withCondition(isCreateOrUpdate)
                                .withRule(new NonNullRule())
                                .withRule(new MaxLengthRule(32))
                )
                .build().validate();;

        RootRule.builder()
                .withFieldName("description")
                .withDto(dto)
                .withContext(context)
                .withCondition(
                        Condition.builder().withCondition(isCreateOrUpdate)
                                .withRule(new MaxLengthRule(100))
                )
                .build().validate();;

        RootRule.builder()
                .withFieldName("status")
                .withDto(dto)
                .withContext(context)
                .withCondition(
                        Condition.builder().withCondition(true)
                                .withRule(new NonNullRule())
                )
                .build().validate();


        return false;
    }
}
