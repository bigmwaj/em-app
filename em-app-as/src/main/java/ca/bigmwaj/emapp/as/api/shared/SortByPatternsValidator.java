package ca.bigmwaj.emapp.as.api.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.SortByItem;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortByPatternsValidator implements ConstraintValidator<ValidSortByPatterns, List<SortByItem>> {

    private ValidSortByPatterns validSortByPatterns;

    @Override
    public void initialize(ValidSortByPatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.validSortByPatterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<SortByItem> sortByItems, ConstraintValidatorContext context) {
        if (sortByItems == null || sortByItems.isEmpty()) {
            return true;
        }

        sortByItems.stream()
                .filter(SortByItem::isValid)
                .forEach(this::validate);

        var errorMessages = sortByItems.stream()
                .map(SortByItem::getValidationErrorMessages)
                .flatMap(List::stream)
                .collect(Collectors.joining(","));

        if (!errorMessages.isEmpty()) {
            context.buildConstraintViolationWithTemplate(errorMessages)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private void validate(SortByItem sortByItem) {
        if (sortByItem.isNotValid()) {
            return;
        }

        validateFieldName(sortByItem);
    }

    private void validateFieldName(SortByItem sortByItem) {
        String fieldName = sortByItem.getName();

        if (fieldName == null) {
            sortByItem.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = getSupportedFieldNames().stream()
                .anyMatch(fieldName::equals);

        if (!isValid) {
            sortByItem.addMessage(String.format(MessageConstants.MSG0006, fieldName));
        }
    }

    List<String> getSupportedFieldNames() {
        return Arrays.stream(validSortByPatterns.supportedFields()).map(SortBySupportedField::name).toList();
    }
}
