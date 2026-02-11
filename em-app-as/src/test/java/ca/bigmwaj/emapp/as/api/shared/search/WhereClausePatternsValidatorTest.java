package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.api.shared.validator.WhereClausePatternsValidator;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class WhereClausePatternsValidatorTest {
    @Test
    void givenWellMappedPatterns_whenIsValid_thenReturnsTrue() {
        // Given
        var supportedFieldNames = List.of("field1", "field2");
        var validator = new WhereClausePatternsValidator();
        var items = List.of(
                new WhereClause("field1", WhereClause.oper.eq, List.of("V1")),
                new WhereClause("field2", WhereClause.oper.ne, List.of("V2")));

        var spyValidator = spy(validator);
        var context = new MockConstraintValidatorContext();
        doReturn(supportedFieldNames).when(spyValidator).getSupportedFieldNames();

        // When
        var isValid = spyValidator.isValid(items, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void givenNotWellMappedPatterns_whenIsValid_thenReturnsTrue() {
        // Given
        var supportedFieldNames = List.of("field1", "field3");
        var validator = new WhereClausePatternsValidator();
        var items = List.of(
                new WhereClause("field1"),
                new WhereClause("field2", WhereClause.oper.eq)
        );
        var spyValidator = spy(validator);
        doReturn(supportedFieldNames).when(spyValidator).getSupportedFieldNames();

        // When
        var isValid = spyValidator.isValid(items, new MockConstraintValidatorContext());

        // Then
        assertFalse(isValid);
    }
}
