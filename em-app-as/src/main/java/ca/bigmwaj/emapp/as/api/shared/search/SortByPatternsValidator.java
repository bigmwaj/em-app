package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.dto.shared.search.SortBy;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortByPatternsValidator implements ConstraintValidator<ValidSortByPatterns, List<SortBy>> {

    private ValidSortByPatterns validSortByPatterns;

    @Override
    public void initialize(ValidSortByPatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.validSortByPatterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<SortBy> sortByItems, ConstraintValidatorContext context) {
        if (sortByItems == null || sortByItems.isEmpty()) {
            return true;
        }

        sortByItems.stream()
                .filter(SortBy::isValid)
                .forEach(this::validate);

        var errorMessages = sortByItems.stream()
                .map(SortBy::getValidationErrorMessages)
                .flatMap(List::stream)
                .collect(Collectors.joining(","));

        if (!errorMessages.isEmpty()) {
            context.buildConstraintViolationWithTemplate(errorMessages)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private void validate(SortBy sortBy) {
        if (sortBy.isNotValid()) {
            return;
        }

        validateFieldName(sortBy);
    }

    private void validateFieldName(SortBy sortBy) {
        String fieldName = sortBy.getName();

        if (fieldName == null) {
            sortBy.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = getSupportedFieldNames().stream()
                .anyMatch(fieldName::equals);

        if (!isValid) {
            sortBy.addMessage(String.format(MessageConstants.MSG0006, fieldName));
        }
    }

    List<String> getSupportedFieldNames() {
        return Arrays.stream(validSortByPatterns.supportedFields()).map(SortBySupportedField::name).toList();
    }
}
