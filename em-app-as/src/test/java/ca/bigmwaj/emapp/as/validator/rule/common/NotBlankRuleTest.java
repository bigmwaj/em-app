package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NotBlankRuleTest extends AbstractRuleTest {

    @BeforeEach
    public void beforeEach() {
        rule = new NotBlankRule();
    }

    @Test
    void test_given_nonStringValue_whenIsValid_then_throwException() {
        assertThrows(ValidationConfigurationException.class, () -> {
            rule.isValid(12, null);
        });
    }

    @Test
    void test_given_nullValue_whenIsValid_then_return_false() {
        boolean isValid = rule.isValid(null, null);
        assertFalse(isValid);
    }

    @Test
    void test_given_emptyValue_whenIsValid_then_return_false() {
        boolean isValid = rule.isValid("", null);
        assertFalse(isValid);
    }

    @Test
    void test_given_blankValue_whenIsValid_then_return_false() {
        boolean isValid = rule.isValid("   \n \t", null);
        assertFalse(isValid);
    }

    @Test
    void test_given_nonNullValue_whenIsValid_then_return_true() {
        boolean isValid = rule.isValid("test", null);
        assertTrue(isValid);
    }
}
