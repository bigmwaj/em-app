package ca.bigmwaj.emapp.as.api.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterPatternsValidator implements ConstraintValidator<ValidFilterPatterns, List<FilterItem>> {

    private ValidFilterPatterns validFilterPatterns;

    @Override
    public void initialize(ValidFilterPatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.validFilterPatterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<FilterItem> filterItems, ConstraintValidatorContext context) {
        if (filterItems == null || filterItems.isEmpty()) {
            return true;
        }

        filterItems.stream()
                .filter(FilterItem::isValid)
                .forEach(this::validate);

        var errorMessages = filterItems.stream()
                .map(FilterItem::getValidationErrorMessages)
                .flatMap(List::stream)
                .collect(Collectors.joining(","));

        if (!errorMessages.isEmpty()) {
            context.buildConstraintViolationWithTemplate(errorMessages)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private void validate(FilterItem filterItem) {
        if (filterItem.isNotValid()) {
            return;
        }

        validateFieldName(filterItem);
        validateOperator(filterItem);
        validateValues(filterItem);
    }

    private void validateFieldName(FilterItem filterItem) {
        String fieldName = filterItem.getName();

        if (fieldName == null) {
            filterItem.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = Arrays.stream(validFilterPatterns.supportedFields())
                .map(FilterSupportedField::name)
                .anyMatch(fieldName::equals);

        if (!isValid) {
            filterItem.addMessage(
                    String.format(MessageConstants.MSG0006, fieldName)
            );
        }
    }

    private void validateOperator(FilterItem filterItem) {
        if (filterItem.getOper() == null) {
            filterItem.addMessage(MessageConstants.MSG0007);
        }
    }

    private void validateValues(FilterItem filterItem) {
        var values = filterItem.getValues();

        if (values == null) {
            filterItem.addMessage(MessageConstants.MSG0008);
            return;
        }

        String message = switch (filterItem.getOper()) {
            case like, eq, ne, lt, lte, gt, gte -> values.size() == 1 ? null : MessageConstants.MSG0009;
            case in, ni -> !values.isEmpty() ? null : MessageConstants.MSG0010;
            case btw -> values.size() == 2 ? null : MessageConstants.MSG0011;
        };

        if (message != null) {
            filterItem.addMessage(message);
        }
    }
}
