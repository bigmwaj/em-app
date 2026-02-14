package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NonEmptyRuleTest extends AbstractRuleTest {

    @BeforeEach
    public void beforeEach() {
        rule = new NonEmptyRule();
    }

    @Test
    void test_given_nonCollectionValue_whenIsValid_then_throwException() {
        assertThrows(ValidationConfigurationException.class, () -> {
            rule.isValid("Any", null);
        });
    }

    @Test
    void test_given_nullValue_whenIsValid_then_return_false() {
        boolean isValid = rule.isValid(null, null);
        assertFalse(isValid);
    }

    @Test
    void test_given_emptyCollection_whenIsValid_then_return_false() {
        boolean isValid = rule.isValid(Collections.emptyList(), null);
        assertFalse(isValid);
    }

    @Test
    void test_given_nonEmptyCollection_whenIsValid_then_return_true() {
        boolean isValid = rule.isValid(List.of("Test"), null);
        assertTrue(isValid);
    }
}
